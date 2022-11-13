package com.hyd.hybatis;

import com.hyd.hybatis.row.Row;
import com.hyd.hybatis.sql.BatchCommand;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Hybatis {

    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    private final HybatisConfiguration configuration;

    private final ConnectionSupplier connectionSupplier;

    @FunctionalInterface
    public interface ConnectionSupplier {

        Connection get() throws SQLException;
    }

    @FunctionalInterface
    public interface RowConsumer {

        void accept(Row row) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionConsumer {

        void accept(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionFunction<T> {

        T accept(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    public interface DatabaseTask {

        void run() throws SQLException;
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

    public void query(String statement, List<Object> parameters, RowConsumer rowConsumer) throws SQLException {
        query(new SqlCommand(statement, parameters), rowConsumer);
    }

    public void query(SqlCommand command, RowConsumer rowConsumer) throws SQLException {
        withConnection(conn -> {
            try (
                var ps = prepareStatement(conn, command);
                var rs = ps.executeQuery()
            ) {
                while (rs.next()) {
                    var row = Row.fromResultSet(rs);
                    rowConsumer.accept(row);
                }
            }
        });
    }

    public Row queryOne(String sql, Object... params) throws SQLException {
        return queryList(sql, params).stream().findFirst().orElse(null);
    }

    public Row queryOne(Sql.Select select) throws SQLException {
        return queryList(select).stream().findFirst().orElse(null);
    }

    public Row queryOne(SqlCommand command) throws SQLException {
        return queryList(command).stream().findFirst().orElse(null);
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
        return withConnection(conn -> {
            var command = update.toCommand();
            return (long) executeCommand(conn, command);
        });
    }

    public long execute(Sql.Delete delete) throws SQLException {
        return withConnection(conn -> {
            var command = delete.toCommand();
            return (long) executeCommand(conn, command);
        });
    }

    public long execute(Sql.Insert insert) throws SQLException {
        return withConnection(conn -> {
            var command = insert.toCommand();
            return (long) executeCommand(conn, command);
        });
    }

    public long execute(String sql, Object... arguments) throws SQLException {
        return withConnection(conn -> {
            var command = new SqlCommand(sql, Arrays.asList(arguments));
            return (long) executeCommand(conn, command);
        });
    }

    public int execute(BatchCommand batchCommand) throws SQLException {
        return withConnection(conn -> {
            var counter = new AtomicInteger();
            try (PreparedStatement ps = conn.prepareStatement(batchCommand.getStatement())) {
                batchCommand.forEachBatch(batchParams -> {
                    for (List<Object> batchParam : batchParams) {
                        setupParameters(ps, batchParam);
                        ps.addBatch();
                    }
                    counter.addAndGet(IntStream.of(ps.executeBatch()).sum());
                });
            }
            return counter.get();
        });
    }

    public int executeBatch(String statement, List<List<Object>> batchParams) throws SQLException {
        return withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (List<Object> batchParam : batchParams) {
                    setupParameters(ps, batchParam);
                    ps.addBatch();
                }
                return IntStream.of(ps.executeBatch()).sum();
            }
        });
    }

    public void runTransaction(DatabaseTask databaseTask) throws HybatisException {
        Connection connection = null;
        try {
            connection = getOrCreateConnection(false);
            currentConnection.set(connection);
            databaseTask.run();
            currentConnection.get().commit();

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    // ignore failure
                }
            }
            throw new HybatisException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore failure
                }
            }
            currentConnection.remove();
        }
    }

    private void withConnection(ConnectionConsumer consumer) throws SQLException {
        var conn = getOrCreateConnection();
        try {
            consumer.accept(conn);
        } finally {
            if (!conn.isClosed() && conn.getAutoCommit()) {
                conn.close();
            }
        }
    }

    private <T> T withConnection(ConnectionFunction<T> function) throws SQLException {
        var conn = getOrCreateConnection();
        try {
            return function.accept(conn);
        } finally {
            if (!conn.isClosed() && conn.getAutoCommit()) {
                conn.close();
            }
        }
    }

    private Connection getOrCreateConnection() throws SQLException {
        return getOrCreateConnection(true);
    }

    private Connection getOrCreateConnection(boolean autoCommit) throws SQLException {
        var currentConn = currentConnection.get();
        if (currentConn != null && !currentConn.isClosed() && !currentConn.getAutoCommit()) {
            return currentConn;
        }

        var connection = this.connectionSupplier.get();
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    private int executeCommand(Connection conn, SqlCommand command) throws SQLException {
        PreparedStatement ps = prepareStatement(conn, command);
        return ps.executeUpdate();
    }

    private static PreparedStatement prepareStatement(Connection conn, SqlCommand command) throws SQLException {
        var ps = conn.prepareStatement(command.getStatement());
        setupParameters(ps, command.getParams());
        return ps;
    }

    private static void setupParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, param);
            }
        }
    }
}
