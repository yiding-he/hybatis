package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public abstract class Expression {

    public abstract SqlCommand toSqlCommand();
}
