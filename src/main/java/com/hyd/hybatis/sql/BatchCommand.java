package com.hyd.hybatis.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 批量操作命令
 */
public class BatchCommand {

    @FunctionalInterface
    public interface BatchConsumer {

        void accept(List<List<Object>> batchParameters) throws SQLException;
    }

    private String statement;               // SQL 语句

    private Stream<List<Object>> params;    // 批量参数组

    private int executeBatchSize = 100;     // 每次执行批量大小

    public BatchCommand() {
    }

    public BatchCommand(String statement, Stream<List<Object>> params) {
        this.statement = statement;
        this.params = params;
    }

    public BatchCommand(String statement, Stream<List<Object>> params, int executeBatchSize) {
        this.statement = statement;
        this.params = params;
        this.executeBatchSize = executeBatchSize;
    }

    public int getExecuteBatchSize() {
        return executeBatchSize;
    }

    public void setExecuteBatchSize(int executeBatchSize) {
        this.executeBatchSize = executeBatchSize;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Stream<List<Object>> getParams() {
        return params;
    }

    public void setParams(Stream<List<Object>> params) {
        this.params = params;
    }

    public void forEachBatch(BatchConsumer batchConsumer) throws SQLException {
        List<List<Object>> batchBuffer = new ArrayList<>(executeBatchSize);
        var iterator = this.params.iterator();
        while (iterator.hasNext()) {
            List<Object> param = iterator.next();
            batchBuffer.add(param);
            if (batchBuffer.size() >= executeBatchSize) {
                batchConsumer.accept(batchBuffer);
                batchBuffer.clear();
            }
        }
        batchConsumer.accept(batchBuffer);
        batchBuffer.clear();
    }

    @Override
    public String toString() {
        return "BatchCommand{" +
            "statement='" + statement + '\'' +
            '}';
    }
}
