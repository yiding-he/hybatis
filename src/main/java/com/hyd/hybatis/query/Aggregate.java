package com.hyd.hybatis.query;

import com.hyd.hybatis.query.aggregate.Count;

public interface Aggregate<A extends Aggregate<A>> {

    static Count count(String field) {
        var count = new Count();
        count.setProjection(Projection.col(field));
        return count;
    }

    Projection getProjection();

    String getAlias();

    String toSqlExpression();
}
