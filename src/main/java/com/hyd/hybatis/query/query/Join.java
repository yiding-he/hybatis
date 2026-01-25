package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.query.QueryContextTracker;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 连接两个二维表对象，得到一个新的二维表。
 */
@Getter
public class Join extends AbstractQuery<Join> {

    public static class JoinBuilder {

        private final Join join = new Join();

        public JoinBuilder(Query leftQuery) {
            join.leftQuery = leftQuery;
        }

        public JoinBuilder with(Query rightQuery) {
            join.rightQuery = rightQuery;
            return this;
        }

        public JoinBuilder type(JoinType joinType) {
            join.joinType = joinType;
            return this;
        }

        public JoinBuilder using(String... joinColumns) {
            join.joinColumns = Stream.of(joinColumns)
                .map(c -> new ColumnPair(Column.exp(c), Column.exp(c)))
                .collect(Collectors.toSet());
            return this;
        }

        public JoinBuilder match(Column leftColumn, Column rightColumn) {
            join.joinColumns.add(new ColumnPair(leftColumn, rightColumn));
            return this;
        }

        public Join build() {
            return join;
        }
    }

    @AllArgsConstructor
    public static class ColumnPair {

        public Column leftColumn;

        public Column rightColumn;
    }

    public enum JoinType {
        Left, Right, Inner
    }

    private Query leftQuery;

    private Query rightQuery;

    private JoinType joinType = JoinType.Left;

    private Set<ColumnPair> joinColumns = new HashSet<>();

    public Join() {
    }

    public Join(Query leftQuery, Query rightQuery, JoinType joinType, String... joinColumns) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.joinType = joinType;
        this.joinColumns = Stream.of(joinColumns)
            .map(c -> new ColumnPair(Column.exp(c), Column.exp(c)))
            .collect(Collectors.toSet());
    }

    @Override
    public SqlCommand getFromFragment() {
        var match = Filter.AND(
            joinColumns.stream()
                .map(p -> Filter.equal(p.leftColumn, p.rightColumn))
                .collect(Collectors.toList())
        );

        // 如果 query 表达式很简单就不用括号，如果复杂就用括号包围
        BiConsumer<SqlCommand, Query> appender = (main, query) -> {
            // 使用函数式接口检测循环引用
            QueryContextTracker.withQuery(query, () -> {
                var append = query.toSqlCommand();
                if (append.getStatement().contains(" ")) {
                    main.append("(" + append.getStatement() + ")", append.getParams());
                } else {
                    main.append(append.getStatement(), append.getParams());
                }
                main.append(query.appendAlias());
            });
        };

        var result = new SqlCommand();
        appender.accept(result, leftQuery);
        result.append(" " + joinType.name().toUpperCase() + " JOIN ");
        appender.accept(result, rightQuery);
        result.append(" ON ").append(match.toSqlFragment());
        return result;
    }
}
