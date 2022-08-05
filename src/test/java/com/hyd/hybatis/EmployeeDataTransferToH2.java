package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transfer data from <a href="https://github.com/datacharmer/test_db">MySQL
 * sample database</a> to H2 database file (already done)
 */
public class EmployeeDataTransferToH2 {

    public static final String[] TABLE_NAMES = {
        "DEPARTMENTS",
        "EMPLOYEES",
        "DEPT_EMP",
        "DEPT_MANAGER",
        "SALARIES",
        "TITLES",
    };

    public static void main(String[] args) throws Exception {
        try (BasicDataSource mysqlDs = new BasicDataSource();
             BasicDataSource h2Ds = new BasicDataSource()) {

            mysqlDs.setDriverClassName("com.mysql.cj.jdbc.Driver");
            mysqlDs.setUrl("jdbc:mysql://localhost:3306/employees");
            mysqlDs.setUsername("root");
            mysqlDs.setPassword("root123");

            h2Ds.setDriverClassName("org.h2.Driver");
            h2Ds.setUrl("jdbc:h2:file:./src/test/data/employees");

            var mysqlHybatis = new Hybatis(null, mysqlDs);
            var h2Hybatis = new Hybatis(null, h2Ds);

            for (String tableName : TABLE_NAMES) {
                transferData(mysqlHybatis, h2Hybatis, tableName);
            }
        }
    }

    private static void transferData(Hybatis mysqlHybatis, Hybatis h2Hybatis, String tableName) throws SQLException {
        System.out.println("Transferring table " + tableName + "...");
        AtomicInteger counter = new AtomicInteger();
        mysqlHybatis.query(Sql.Select("*").From(tableName), row -> {
            var insert = Sql.Insert(tableName).Values(row);
            h2Hybatis.execute(insert);
            counter.incrementAndGet();
        });
        System.out.println("Transferring completed with " + counter.get() + " rows.");
    }

}
