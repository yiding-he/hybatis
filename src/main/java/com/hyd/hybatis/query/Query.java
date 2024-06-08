package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.QueryColumn;
import com.hyd.hybatis.query.query.Join;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hyd.hybatis.utils.Obj.isNotEmpty;
import static java.util.Collections.emptyList;

/**
 * 表示一个查询结构，查询结构与查询结构之间可以相互组合，以实现查询结构的复用。
 */
public interface Query<Q extends Query<Q>> extends Alias, Limit {

    /**
     * 过滤条件列表
     */
    List<Match> getMatches();

    /**
     * 直接选取的字段列表
     */
    List<Column<?>> getColumns();

    /**
     * 分组字段列表
     */
    List<Column<?>> getGroupBy();

    ////////////////////////////////////////

    /**
     * 选取指定的字段
     *
     * @param column 字段名或表达式
     */
    default QueryColumn col(String column) {
        if (Obj.isEmpty(column)) {
            return null;
        } else {
            return new QueryColumn(this, column, null);
        }
    }

    /**
     * 选取指定的字段
     */
    default List<Column<?>> cols(String... columns) {
        if (columns == null) {
            return emptyList();
        }
        return Stream.of(columns)
            .map(this::col)
            .filter(Obj::isNotEmpty)
            .collect(Collectors.toList());
    }

    default SqlCommand getSelectFragment() {
        var columnsList = new ArrayList<String>();
        var paramsList = new ArrayList<>();

        // 从 getProjections() 拼接查询字段
        this.getColumns().forEach(c -> {
            var f = c.toSqlFragment();
            columnsList.add(f.getStatement());
            paramsList.addAll(f.getParams());
        });

        // 从 getGroupBy() 拼接分组字段
        this.getGroupBy().stream().map(Column::toSqlFragment).forEach(f -> {
            columnsList.add(f.getStatement());
            paramsList.addAll(f.getParams());
        });

        if (columnsList.isEmpty()) {
            return new SqlCommand("*", emptyList());
        } else {
            return new SqlCommand(String.join(",", columnsList), paramsList);
        }
    }

    default SqlCommand getGroupByFragment() {
        var groupBy = getGroupBy();
        if (groupBy.isEmpty()) {
            return new SqlCommand("");
        }
        return new SqlCommand(" GROUP BY " + groupBy.stream()
            .map(Column::toSqlFragment)
            .map(SqlCommand::getStatement)
            .collect(Collectors.joining(","))
        );
    }

    ////////////////////////////////////////

    default Join leftJoin(Query<?> other, String... joinColumns) {
        return new Join(this, other, Join.JoinType.Left, joinColumns);
    }

    default Join rightJoin(Query<?> other, String... joinColumns) {
        return new Join(this, other, Join.JoinType.Right, joinColumns);
    }

    ////////////////////////////////////////

    /**
     * 生成 SQL Command 对象
     */
    default SqlCommand toSqlCommand() {
        var sqlCommand = new SqlCommand("SELECT ")
            .append(getSelectFragment())
            .append(" FROM ")
            .append(getFromFragment())
            .append(appendAlias());

        if (isNotEmpty(this.getMatches())) {
            sqlCommand = sqlCommand
                .append(" WHERE ")
                .append(Match.AND(this.getMatches()).toSqlFragment());
        }

        // TODO 自动添加 group by
        return sqlCommand
            .append(getGroupByFragment())
            .append(appendLimit());
    }

    /**
     * 生成 From 子句内容，可以是表名/视图名，也可以是一个子查询
     */
    SqlCommand getFromFragment();
}
