package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public class AttributeExpression extends Expression {

    private final String name;

    public AttributeExpression(String name) {
        super(null);
        this.name = name;
    }

    @Override
    public SqlCommand toSqlCommand() {
        return new SqlCommand(name);
    }
}
