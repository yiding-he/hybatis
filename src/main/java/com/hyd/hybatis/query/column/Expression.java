package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public abstract class Expression {

    private final Expression anchor;

    public Expression(Expression anchor) {
        this.anchor = anchor;
    }

    public Expression getAnchor() {
        return anchor;
    }

    public abstract SqlCommand toSqlCommand();
}
