package com.hyd.hybatis.query;

import com.hyd.hybatis.query.projection.PlainProjection;
import com.hyd.hybatis.query.projection.QueryProjection;
import com.hyd.hybatis.utils.Str;

/**
 * 表示要选取的字段，例如
 * <p>{@code var projection = Projection.from(query).with("name").as("nickname")}</p>
 * <p>{@code var projection = Projection.plain("name").as("nickname")}</p>
 */
public interface Projection {

    /**
     * 定义从 Query 中选取的字段
     */
    static QueryProjection from(Query query) {
        return QueryProjection.from(query);
    }

    /**
     * 定义从表名或 Query 的别名中选取的字段
     */
    static QueryProjection from(String from) {
        return QueryProjection.from(from);
    }

    /**
     * 直接定义要选取的字段
     */
    static PlainProjection col(String field) {
        return new PlainProjection().with(field);
    }

    /**
     * 字段来源，通常是另一个 Query 的别名
     */
    String getFrom();

    /**
     * 字段名
     */
    String getField();

    /**
     * 字段别名
     */
    String getAlias();

    default String toSqlExpression() {
        return (Str.isBlank(getFrom())? "": (getFrom() + ".")) +
            getField() +
            (Str.isBlank(getAlias())? "": " AS " + getAlias());
    }
}
