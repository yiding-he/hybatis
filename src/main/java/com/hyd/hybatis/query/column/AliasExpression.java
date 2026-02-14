package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public class AliasExpression extends Expression {

    private final Expression expression;
    private final String alias;

    public AliasExpression(Expression expression, String alias) {
        super(expression);
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public SqlCommand toSqlCommand() {
        return expression.toSqlCommand().append(" AS ").append(alias);
    }
}
