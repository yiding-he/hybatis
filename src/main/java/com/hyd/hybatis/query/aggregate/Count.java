package com.hyd.hybatis.query.aggregate;

public class Count extends AbstractAggregate<Count> {

    @Override
    public String toSqlExpression() {
        return "COUNT(" + getProjection().toSqlExpression() + ")"
            + (getAlias() != null ? " AS " + getAlias() : "");
    }
}
