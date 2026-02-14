package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;

public class SimpleColumn implements Column {

    private final Expression expression;

    public SimpleColumn(Expression expression) {
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
