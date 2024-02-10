package com.hyd.hybatis.utils;

import java.sql.SQLException;

/**
 * Collection of functional interfaces.
 */
public interface Functional {

    /**
     * A function which throws SQLException.
     */
    @FunctionalInterface
    interface SqlFunction {

        void run() throws SQLException;

        default Runnable toRunnable() {
            return () -> {
                try {
                    run();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }
}
