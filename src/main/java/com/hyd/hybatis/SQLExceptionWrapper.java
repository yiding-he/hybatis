package com.hyd.hybatis;

import java.sql.SQLException;

/**
 * Wrap {@link SQLException} into a runtime exception
 */
public class SQLExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = 3016423970838673147L;

    public static SQLExceptionWrapper wrap(SQLException e) {
        return new SQLExceptionWrapper(e);
    }

    private final SQLException cause;

    public SQLExceptionWrapper(SQLException cause) {
        this.cause = cause;
    }

    public SQLException unwrap() {
        return this.cause;
    }
}
