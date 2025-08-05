package com.hyd.hybatis;

import com.hyd.hybatis.jdbc.resultset.ResultSetBeanIterator;
import com.hyd.hybatis.jdbc.resultset.ResultSetRowIterator;
import com.hyd.hybatis.row.Row;
import com.hyd.hybatis.sql.BatchCommand;
import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
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

@Slf4j
public class Hybatis {

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

    @Getter
    private final HybatisConfiguration configuration;

    private final DataSource dataSource;

    private final Configuration mybatisConfiguration;

    private final TransactionFactory transactionFactory;

    @FunctionalInterface
    public interface RowConsumer {

        void accept(Row row) throws SQLException;
    }

    @FunctionalInterface
    public interface EntityConsumer<T> {

        void accept(T entity) throws SQLException;
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
        SqlSessionFactory sqlSessionFactory
    ) {
        this.configuration = configuration;
        this.mybatisConfiguration = sqlSessionFactory.getConfiguration();
        this.dataSource = this.mybatisConfiguration.getEnvironment().getDataSource();
        this.transactionFactory = this.mybatisConfiguration.getEnvironment().getTransactionFactory();
    }

    /**
     * @deprecated 为了兼容旧版本的构造方法，构造出来的 Hybatis 对象无法将查询结果转换为 java bean
     */
    @Deprecated
    public Hybatis(
        HybatisConfiguration configuration,
        DataSource dataSource
    ) {
        this.configuration = configuration;
        this.dataSource = dataSource;
        this.mybatisConfiguration = null;
        this.transactionFactory = new JdbcTransactionFactory();
    }

    //////////////////////////////////////// Query methods which return Row objects

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

    //////////////////////////////////////// Query methods which return entity objects

    public <T> void query(
        Class<T> entityClass, Sql.Select select, EntityConsumer<T> entityConsumer
    ) throws SQLException {
        query(entityClass, select.toCommand(), entityConsumer);
    }

    public <T> void query(
        Class<T> entityClass, String statement, List<Object> parameters, EntityConsumer<T> entityConsumer
    ) throws SQLException {
        query(entityClass, new SqlCommand(statement, parameters), entityConsumer);
    }

    public <T> void query(
        Class<T> entityClass, SqlCommand command, EntityConsumer<T> entityConsumer
    ) throws SQLException {
        try (var entityStream = queryStream(entityClass, command)) {
            try {
                entityStream.forEach(entity -> {
                    try {
                        entityConsumer.accept(entity);
                    } catch (SQLException e) {
                        throw new SQLExceptionWrapper(e);
                    }
                });
            } catch (SQLExceptionWrapper w) {
                throw w.unwrap();
            }
        }
    }

    public <T> T queryOne(Class<T> entityClass, String sql, Object... params) throws SQLException {
        return queryOne(entityClass, new SqlCommand(sql, List.of(params)));
    }

    public <T> T queryOne(Class<T> entityClass, Sql.Select select) throws SQLException {
        return queryOne(entityClass, select.toCommand());
    }

    public <T> T queryOne(Class<T> entityClass, SqlCommand command) throws SQLException {
        try (var entityStream = queryStream(entityClass, command)) {
            return entityStream.findFirst().orElse(null);
        }
    }

    public <T> List<T> queryList(Class<T> entityClass, String sql, Object... params) throws SQLException {
        return queryList(entityClass, new SqlCommand(sql, Arrays.asList(params)));
    }

    public <T> List<T> queryList(Class<T> entityClass, Sql.Select select) throws SQLException {
        return queryList(entityClass, select.toCommand());
    }

    public <T> List<T> queryList(Class<T> entityClass, SqlCommand command) throws SQLException {
        try (var entityStream = queryStream(entityClass, command)) {
            return entityStream.collect(Collectors.toList());
        }
    }

    public <T> Stream<T> queryStream(Class<T> entityClass, String sql, Object... params) throws SQLException {
        return queryStream(entityClass, new SqlCommand(sql, List.of(params)));
    }

    public <T> Stream<T> queryStream(Class<T> entityClass, Sql.Select select) throws SQLException {
        return queryStream(entityClass, select.toCommand());
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
            return new ResultSetRowIterator(rs)
                .toRowStream()
                .onClose(() -> closeWhenNecessary(conn, true));
        }, false);
    }

    public <T> Stream<T> queryStream(Class<T> entityClass, SqlCommand command) throws SQLException {
        if (this.mybatisConfiguration == null) {
            throw new HybatisException("Hybatis 没有正确初始化，请使用另外的构造方法");
        }

        return withConnection(conn -> {
            var ps = prepareStatement(conn, command);
            var rs = ps.executeQuery();
            return new ResultSetBeanIterator<>(this.mybatisConfiguration, rs, entityClass)
                .toBeanStream()
                .onClose(() -> closeWhenNecessary(conn, true));
        }, false);
    }

    //////////////////////////////////////// Execute methods like insert, update, delete

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

    private Transaction getTransaction() {
        // 在 Spring 框架中，newTransaction() 方法的后面两个参数不起作用
        return transactionFactory.newTransaction(dataSource, null, false);
    }

    /**
     * 通过 @Transactional 注解，让 Spring 框架开启事务
     * 如果已经在事务中则取现有的事务, 否则就创建一个新的事务
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void runTransaction(DatabaseTask databaseTask) throws HybatisException {
        Transaction transaction = null;
        try {
            transaction = getTransaction();
            databaseTask.run();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (SQLException ex) {
                    log.warn("Transaction rollback failed", ex);
                }
            }
            throw new HybatisException(e);
        } finally {
            if (transaction != null) {
                try {
                    transaction.close();
                } catch (SQLException e) {
                    log.warn("Transaction close failed", e);
                }
            }
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
        return DataSourceUtils.getConnection(dataSource);
    }

    private int executeCommand(Connection conn, SqlCommand command) throws SQLException {
        var ps = prepareStatement(conn, command);
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
