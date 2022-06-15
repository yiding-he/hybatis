package com.hyd.hybatis;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.sql.SQLException;

public class MyBatisConfigurationBuilder {

    public static Configuration build() throws SQLException {
        var ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:mem:test");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setDriverClassName("org.h2.Driver");

        try (var conn = ds.getConnection()) {
            conn.createStatement().execute("create table users (user_id int primary key, user_name varchar(20))");
            conn.createStatement().execute("insert into users values (1, 'Zhang San')");
            conn.createStatement().execute("insert into users values (2, 'Li Si')");
        }

        //////////////////////////

        JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("default", transactionFactory, ds);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }
}
