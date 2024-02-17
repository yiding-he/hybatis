package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.Col;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hyd.hybatis.utils.Obj.defaultValue;
import static com.hyd.hybatis.utils.Obj.isNotEmpty;

/**
 * 表示一个查询结构，查询结构与查询结构之间可以相互组合，以实现查询结构的复用。
 */
public interface Query<Q extends Query<Q>> extends Alias {

    /**
     * 过滤条件列表
     */
    List<Match> getMatches();

    /**
     * 聚合操作列表
     */
    List<Aggregate<?>> getAggregates();

    /**
     * 直接选取的字段列表
     */
    List<Column<?>> getColumns();

    /**
     * 跳过的行数
     */
    default int getSkip() {
        return 0;
    }

    /**
     * 最多返回记录数，-1 表示不限制
     */
    default int getLimit() {
        return -1;
    }

    ////////////////////////////////////////

    /**
     * 选取指定的字段
     *
     * @param column 字段名或表达式
     */
    default Col col(String column) {
        if (Obj.isEmpty(column)) {
            return null;
        } else {
            return new Col(this, column, null);
        }
    }

    /**
     * 选取指定的字段
     */
    default List<Column<?>> cols(String... columns) {
        if (columns == null) {
            return Collections.emptyList();
        }
        return Stream.of(columns)
            .map(this::col)
            .filter(Obj::isNotEmpty)
            .collect(Collectors.toList());
    }

    default String getProjectionsStatement() {
        var list = new ArrayList<String>();

        // 从 getProjections() 拼接查询字段
        list.addAll(
            this.getColumns().stream()
                .map(Column::toSqlExpression)
                .collect(Collectors.toList())
        );

        // 从 getAggregates() 拼接查询字段
        list.addAll(
            this.getAggregates().stream()
                .map(Aggregate::toSqlExpression)
                .collect(Collectors.toList())
        );

        return String.join(",", list);
    }

    ////////////////////////////////////////

    /**
     * 生成 SQL Command 对象
     */
    default SqlCommand toSqlCommand() {

        var fromSegment = getFromSegment();
        var params = new ArrayList<>(fromSegment.getParams());

        var statement = "SELECT " + defaultValue(getProjectionsStatement(), "*") +
            " FROM " + fromSegment.getStatement() + appendAlias();

        if (isNotEmpty(this.getMatches())) {
            var matchSql = Match.AND(this.getMatches()).toSqlCommand();
            statement += " WHERE " + matchSql.getStatement();
            params.addAll(matchSql.getParams());
        }

        return new SqlCommand(statement, params);
    }

    SqlCommand getFromSegment();
}
