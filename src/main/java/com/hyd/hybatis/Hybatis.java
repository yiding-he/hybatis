package com.hyd.hybatis;

import com.hyd.hybatis.row.Row;
import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlCommand;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hybatis {

    private final HybatisConfiguration configuration;

    private final ConnectionSupplier connectionSupplier;

    public interface ConnectionSupplier {

        Connection get() throws SQLException;
    }

    public interface RowConsumer {

        void accept(Row row) throws SQLException;
    }

    public Hybatis(HybatisConfiguration configuration, ConnectionSupplier connectionSupplier) {
        this.configuration = configuration;
        this.connectionSupplier = connectionSupplier;
    }

    public Hybatis(HybatisConfiguration configuration, DataSource dataSource) {
        this(configuration, dataSource::getConnection);
    }

    public Hybatis(HybatisConfiguration configuration, SqlSessionFactory sqlSessionFactory) {
        this(configuration,
            () -> sqlSessionFactory.getConfiguration().getEnvironment().getDataSource().getConnection());
    }

    public void query(Sql.Select select, RowConsumer rowConsumer) throws SQLException {
        query(select.toCommand(), rowConsumer);
    }

    public void query(SqlCommand command, RowConsumer rowConsumer) throws SQLException {
        try (
            var conn = getConnection();
            var ps = prepareStatement(conn, command);
            var rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                var row = Row.fromResultSet(rs);
                rowConsumer.accept(row);
            }
        }
    }

    public List<Row> queryList(String sql, Object... params) throws SQLException {
        return queryList(new SqlCommand(sql, Arrays.asList(params)));
    }

    public List<Row> queryList(Sql.Select select) throws SQLException {
        return queryList(select.toCommand());
    }

    public List<Row> queryList(SqlCommand command) throws SQLException {
        List<Row> rowList = new ArrayList<>();
        query(command, rowList::add);
        return rowList;
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
        return this.connectionSupplier.get();
    }

    private int executeCommand(Connection conn, SqlCommand command) throws SQLException {
        PreparedStatement ps = prepareStatement(conn, command);
        return ps.executeUpdate();
    }

    private static PreparedStatement prepareStatement(Connection conn, SqlCommand command) throws SQLException {
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
        return ps;
    }
}
