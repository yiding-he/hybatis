package com.hyd.hybatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BatchExecutor {

    private final Hybatis hybatis;

    private final String statement;

    private final int batchSize;

    private final List<List<Object>> parameterBuffer = new ArrayList<>();

    public BatchExecutor(Hybatis hybatis, String statement, int batchSize) {
        this.hybatis = hybatis;
        this.statement = statement;
        this.batchSize = batchSize;
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

    public synchronized void feed(List<Object> parameters) throws SQLException {
        this.parameterBuffer.add(parameters);
        if (this.parameterBuffer.size() >= this.batchSize) {
            this.hybatis.executeBatch(this.statement, this.parameterBuffer);
            this.parameterBuffer.clear();
        }
    }

    public synchronized void finish() throws SQLException{
        if (this.parameterBuffer.isEmpty()) {
            return;
        }

        this.hybatis.executeBatch(this.statement, this.parameterBuffer);
        this.parameterBuffer.clear();
    }
}
