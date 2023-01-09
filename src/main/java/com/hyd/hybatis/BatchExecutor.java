package com.hyd.hybatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BatchExecutor {

    private final Hybatis hybatis;

    private final String statement;

    private final int batchSize;

    private final List<List<Object>> parameterBuffer = new ArrayList<>();

    private final Consumer<Long> onFlush;

    private volatile long counter = 0L;

    public BatchExecutor(Hybatis hybatis, String statement, int batchSize) {
        this(hybatis, statement, batchSize, null);
    }

    public BatchExecutor(Hybatis hybatis, String statement, int batchSize, Consumer<Long> onFlush) {
        this.hybatis = hybatis;
        this.statement = statement;
        this.batchSize = batchSize;
        this.onFlush = onFlush;
    }

    public Hybatis getHybatis() {
        return hybatis;
    }

    public String getStatement() {
        return statement;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public long getCounter() {
        return counter;
    }

    public synchronized void resetCounter() {
        this.counter = 0;
    }

    public synchronized void feed(List<Object> parameters) throws SQLException {
        this.parameterBuffer.add(parameters);
        if (this.parameterBuffer.size() >= this.batchSize) {
            flush();
        }
    }

    public synchronized void finish() throws SQLException{
        if (this.parameterBuffer.isEmpty()) {
            return;
        }
        flush();
    }

    private void flush() throws SQLException {
        this.hybatis.executeBatch(this.statement, this.parameterBuffer);
        this.counter += this.parameterBuffer.size();
        this.parameterBuffer.clear();
        if (onFlush != null) {
            onFlush.accept(this.counter);
        }
    }
}
