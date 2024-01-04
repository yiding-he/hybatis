package com.hyd.hybatis;

import com.hyd.hybatis.row.Row;
import com.hyd.hybatis.sql.BatchCommand;
import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.ResultSetIterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.ibatis.session.TransactionIsolationLevel.READ_COMMITTED;

@Slf4j
public class Hybatis {

    private static final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();

    private static void closeWhenNecessary(Connection conn, boolean doCloseByDefault) {
        try {
            log.trace("Try closing connection, isClosed={}, autoCommit={}, doCloseByDefault={}",
                conn.isClosed(), conn.getAutoCommit(), doCloseByDefault);
            if (doCloseByDefault && !conn.isClosed() && conn.getAutoCommit()) {
                conn.close();
            }
        } catch (Exception e) {
            log.warn("Connection close failed: {}", e.toString());
        }
    }

    private final HybatisConfiguration configuration;

    private final DataSource dataSource;

    /**
     * Provided by MyBatis, use it for compatibility
     */
    private final TransactionFactory transactionFactory;

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

    public Hybatis(
        HybatisConfiguration configuration,
        DataSource dataSource,
        TransactionFactory transactionFactory
    ) {
        this.configuration = configuration;
        this.dataSource = dataSource;
        this.transactionFactory = transactionFactory;
    }

    public Hybatis(DataSource dataSource) {
        this(new HybatisConfiguration(), dataSource);
    }

    public Hybatis(HybatisConfiguration configuration, DataSource dataSource) {
        this(configuration, dataSource, new JdbcTransactionFactory());
    }

    public Hybatis(HybatisConfiguration configuration, SqlSessionFactory sqlSessionFactory) {
        this(
            configuration,
            sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            sqlSessionFactory.getConfiguration().getEnvironment().getTransactionFactory()
        );
    }

    public HybatisConfiguration getConfiguration() {
        return configuration;
    }

    public void query(Sql.Select select, RowConsumer rowConsumer) throws SQLException {
        query(select.toCommand(), rowConsumer);
    }

    public void query(String statement, List<Object> parameters, RowConsumer rowConsumer) throws SQLException {
        query(new SqlCommand(statement, parameters), rowConsumer);
    }

    public void query(SqlCommand command, RowConsumer rowConsumer) throws SQLException {
        try (var rowStream = queryStream(command)) {
            try {
                rowStream.forEach(row -> {
                    try {
                        rowConsumer.accept(row);
                    } catch (SQLException e) {
                        throw new SQLExceptionWrapper(e);
                    }
                });
            } catch (SQLExceptionWrapper w) {
                throw w.unwrap();
            }
        }
    }

    public Row queryOne(String sql, Object... params) throws SQLException {
        return queryOne(new SqlCommand(sql, List.of(params)));
    }

    public Row queryOne(Sql.Select select) throws SQLException {
        return queryOne(select.toCommand());
    }

    public Row queryOne(SqlCommand command) throws SQLException {
        try (var rowStream = queryStream(command)) {
            return rowStream.findFirst().orElse(null);
        }
    }

    public List<Row> queryList(String sql, Object... params) throws SQLException {
        return queryList(new SqlCommand(sql, Arrays.asList(params)));
    }

    public List<Row> queryList(Sql.Select select) throws SQLException {
        return queryList(select.toCommand());
    }

    public List<Row> queryList(SqlCommand command) throws SQLException {
        try (var rowStream = queryStream(command)) {
            return rowStream.collect(Collectors.toList());
        }
    }

    public Stream<Row> queryStream(String sql, Object... params) throws SQLException {
        return queryStream(new SqlCommand(sql, List.of(params)));
    }

    public Stream<Row> queryStream(Sql.Select select) throws SQLException {
        return queryStream(select.toCommand());
    }

    /**
     * The core query method.
     * <p>
     * <b>CAUTION: return object must be closed manually.</b>
     *
     * @return Stream of {@code Row} objects. Must be closed manually.
     */
    public Stream<Row> queryStream(SqlCommand command) throws SQLException {
        return withConnection(conn -> {
            var ps = prepareStatement(conn, command);
            var rs = ps.executeQuery();
            return new ResultSetIterator(rs)
                .toRowStream()
                .onClose(() -> closeWhenNecessary(conn, true));
        }, false);
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
        return executeBatch(batchCommand.getStatement(), batchCommand.getParams());
    }

    public int executeBatch(String statement, List<List<Object>> batchParams) throws SQLException {
        return executeBatch(statement, batchParams.stream());
    }

    public int executeBatch(String statement, Stream<List<Object>> batchParams) throws SQLException {
        return withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                batchParams.forEach(batchParam -> {
                    try {
                        setupParameters(ps, batchParam);
                        ps.addBatch();
                    } catch (SQLException e) {
                        throw SQLExceptionWrapper.wrap(e);
                    }
                });
                return IntStream.of(ps.executeBatch()).sum();
            } catch (SQLExceptionWrapper w) {
                throw w.unwrap();
            }
        });
    }

    @Transactional
    public void runTransaction(DatabaseTask databaseTask) throws HybatisException {
        try {
            // 在 Spring 框架中，newTransaction() 方法的后面两个参数不起作用
            var transaction = transactionFactory.newTransaction(dataSource, READ_COMMITTED, false);
            currentTransaction.set(transaction);
            databaseTask.run();
            currentTransaction.get().commit();

        } catch (Exception e) {
            if (currentTransaction.get() != null) {
                try {
                    currentTransaction.get().rollback();
                } catch (SQLException ex) {
                    log.warn("Transaction rollback failed: {}", ex.toString());
                }
            }
            throw new HybatisException(e);
        } finally {
            if (currentTransaction.get() != null) {
                try {
                    currentTransaction.get().close();
                } catch (SQLException e) {
                    log.warn("Transaction close failed: {}", e.toString());
                }
            }
            currentTransaction.remove();
        }
    }

    private void withConnection(ConnectionConsumer consumer) throws SQLException {
        withConnection(consumer, true);
    }

    private void withConnection(ConnectionConsumer consumer, boolean autoClose) throws SQLException {
        var conn = getOrCreateConnection();
        try {
            consumer.accept(conn);
        } finally {
            closeWhenNecessary(conn, autoClose);
        }
    }

    private <T> T withConnection(ConnectionFunction<T> function) throws SQLException {
        return withConnection(function, true);
    }

    private <T> T withConnection(ConnectionFunction<T> function, boolean autoClose) throws SQLException {
        var conn = getOrCreateConnection();
        try {
            return function.accept(conn);
        } finally {
            closeWhenNecessary(conn, autoClose);
        }
    }

    private Connection getOrCreateConnection() throws SQLException {
        return getOrCreateConnection(true);
    }

    private Connection getOrCreateConnection(boolean autoCommit) throws SQLException {
        Connection connection;
        if (currentTransaction.get() != null) {
            connection = currentTransaction.get().getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } else {
            connection = DataSourceUtils.getConnection(dataSource);
        }

        // 如果是 DataSourceUtils 新创建的连接，而不在事务当中，则可以随意设置 autoCommit
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    private int executeCommand(Connection conn, SqlCommand command) throws SQLException {
        PreparedStatement ps = prepareStatement(conn, command);
        return ps.executeUpdate();
    }

    private static PreparedStatement prepareStatement(Connection conn, SqlCommand command) throws SQLException {
        log.debug("Preparing sql: {} {}", command.getStatement(), command.getParams());
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
