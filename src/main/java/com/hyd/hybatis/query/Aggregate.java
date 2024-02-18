package com.hyd.hybatis.query;

import com.hyd.hybatis.query.aggregate.Count;
import com.hyd.hybatis.sql.SqlCommand;

public interface Aggregate<A extends Aggregate<A>> extends Alias {

    static Count count(Column<?> column) {
        var count = new Count();
        count.setColumn(column);
        return count;
    }

    SqlCommand toSqlFragment();
}
