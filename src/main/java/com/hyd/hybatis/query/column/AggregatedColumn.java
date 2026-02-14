package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;

public class AggregatedColumn implements Column {

    private final AggregateExpression expression;

    public AggregatedColumn(AggregateExpression expression) {
        this.expression = expression;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String getName() {
        return expression.toSqlCommand().getStatement();
    }
}
