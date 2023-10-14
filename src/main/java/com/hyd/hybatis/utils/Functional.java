package com.hyd.hybatis.utils;

import java.sql.SQLException;

public interface Functional {

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
