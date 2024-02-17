package com.hyd.hybatis.query;

import com.hyd.hybatis.query.aggregate.Count;

public interface Aggregate<A extends Aggregate<A>> extends Alias {

    static Count count(Column<?> column) {
        var count = new Count();
        count.setColumn(column);
        return count;
    }

    String toSqlExpression();
}
