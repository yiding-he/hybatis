package com.hyd.hybatis.query.aggregate;

import com.hyd.hybatis.sql.SqlCommand;

public class Count extends SingleColumnAggregate<Count> {

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand("COUNT(")
            .append(getColumn().toSqlFragment())
            .append(")")
            .append(appendAlias());
    }
}
