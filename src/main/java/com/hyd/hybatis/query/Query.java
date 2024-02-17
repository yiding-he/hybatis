package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示一个查询结构，查询结构与查询结构之间可以相互组合，以实现查询结构的复用。
 */
public interface Query<Q extends Query<Q>> {

    /**
     * 定义过滤条件
     */
    List<Match> getMatches();

    /**
     * 定义聚合操作
     */
    List<Aggregate<?>> getAggregates();

    /**
     * 定义选取字段
     */
    List<Projection> getProjections();

    /**
     * 当前查询的别名
     */
    String getAlias();

    /**
     * 生成 SQL Command 对象
     */
    SqlCommand toSqlCommand();

    ////////////////////////////////////////

    /**
     * 选取指定的字段
     *
     * @param column 字段名或表达式
     */
    default Projection col(String column) {
        if (Obj.isEmpty(column)) {
            return null;
        } else {
            return Projection.from(this).col(column);
        }
    }

    /**
     * 选取指定的字段
     */
    default List<Projection> cols(String... columns) {
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
            this.getProjections().stream()
                .map(Projection::toSqlExpression)
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

}
