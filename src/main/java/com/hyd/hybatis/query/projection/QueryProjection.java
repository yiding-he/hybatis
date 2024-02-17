package com.hyd.hybatis.query.projection;

import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.Query;
import lombok.Data;

/**
 * {@link Projection} 的默认实现
 */
@Data
public class QueryProjection implements Projection {

    public static QueryProjection from(Query<?> query) {
        var p = new QueryProjection();
        p.from = query;
        return p;
    }

    private Query<?> from;

    private String field;

    private String alias;

    @Override
    public String getFrom() {
        return from.getAlias();
    }

    public QueryProjection col(String field) {
        this.field = field;
        return this;
    }

    public QueryProjection as(String alias) {
        this.alias = alias;
        return this;
    }
}
