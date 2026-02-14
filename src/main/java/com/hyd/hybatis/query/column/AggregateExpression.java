package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public class AggregateExpression extends Expression {

    private final String aggFunc;
    private final Expression column;

    public AggregateExpression(String aggFunc, Expression column) {
        super(column);
        this.aggFunc = aggFunc;
        this.column = column;
    }

    @Override
    public SqlCommand toSqlCommand() {
        return new SqlCommand(aggFunc + "(").append(column.toSqlCommand()).append(")");
    }
}
