package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlCommand;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

public class Hybatis {

    private final HybatisConfiguration configuration;

    private final SqlSessionFactory sqlSessionFactory;

    public Hybatis(HybatisConfiguration configuration, SqlSessionFactory sqlSessionFactory) {
        this.configuration = configuration;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public long execute(Sql.Update update) throws SQLException {
        try (var conn = getConnection()) {
            var command = update.toCommand();
            return executeCommand(conn, command);
        }
    }

    public long execute(Sql.Delete delete) throws SQLException {
        try (var conn = getConnection()) {
            var command = delete.toCommand();
            return executeCommand(conn, command);
        }
    }

    public long execute(Sql.Insert insert) throws SQLException {
        try (var conn = getConnection()) {
            var command = insert.toCommand();
            return executeCommand(conn, command);
        }
    }

    public long execute(String sql, Object... arguments) throws SQLException {
        try (var conn = getConnection()) {
            var command = new SqlCommand(sql, Arrays.asList(arguments));
            return executeCommand(conn, command);
        }
    }

    private Connection getConnection() throws SQLException {
        var env = this.sqlSessionFactory.getConfiguration().getEnvironment();
        return env.getDataSource().getConnection();
    }

    private int executeCommand(Connection conn, SqlCommand command) throws SQLException {
        var ps = conn.prepareStatement(command.getStatement());
        List<Object> params = command.getParams();
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, param);
            }
        }
        return ps.executeUpdate();
    }
}
