package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public class BinaryExpression extends Expression {

    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public SqlCommand toSqlCommand() {
        return new SqlCommand()
            .append(left.toSqlCommand())
            .append(" ")
            .append(operator)
            .append(" ")
            .append(right.toSqlCommand());
    }
}
