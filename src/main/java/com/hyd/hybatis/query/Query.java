package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hyd.hybatis.utils.Obj.isNotEmpty;

/**
 * 表示一个查询结构，查询结构与查询结构之间可以相互组合，以实现查询结构的复用。
 */
public interface Query {

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

    SqlCommand toSqlCommand();

    ////////////////////////////////////////

    default String getProjectionsStatement() {
        var list = new ArrayList<String>();

        // 从 getProjections() 拼接查询字段
        list.addAll(
            this.getProjections().stream()
                .map(Projection::toSqlExpression)
                .collect(Collectors.toList())
        );

        // TODO 从 getAggregates() 拼接查询字段

        return String.join(",", list);
    }

}
