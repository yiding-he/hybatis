package com.hyd.hybatis.query.projection;

import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.Query;
import lombok.Data;

/**
 * {@link Projection} 的默认实现
 */
@Data
public class QueryProjection implements Projection {

    public static QueryProjection from(Query query) {
        return from(query.getAlias());
    }

    public static QueryProjection from(String from) {
        var p = new QueryProjection();
        p.from = from;
        return p;
    }

    private String from;

    private String field;

    private String alias;

    public QueryProjection col(String field) {
        this.field = field;
        return this;
    }

    public QueryProjection as(String alias) {
        this.alias = alias;
        return this;
    }
}
