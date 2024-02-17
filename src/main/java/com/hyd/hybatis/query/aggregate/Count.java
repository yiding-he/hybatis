package com.hyd.hybatis.query.aggregate;

public class Count extends SingleColumnAggregate<Count> {

    @Override
    public String toSqlExpression() {
        return "COUNT(" + getColumn().toSqlExpression() + ")" + appendAlias();
    }
}
