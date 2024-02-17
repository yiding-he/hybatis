package com.hyd.hybatis.query;

import com.hyd.hybatis.query.aggregate.Count;

public interface Aggregate<A extends Aggregate<A>> {

    static Count count(Projection projection) {
        var count = new Count();
        count.setProjection(projection);
        return count;
    }

    Projection getProjection();

    String getAlias();

    String toSqlExpression();
}
