package com.hyd.hybatis;

import com.hyd.hybatis.utils.Functional.SqlFunction;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * The BatchExecutor is a tool for executing SQL bulk operations.
 * Execution can be performed synchronously or asynchronously
 * based on the availability of a thread pool.
 */
@Slf4j
public class BatchExecutor {

    private final Hybatis hybatis;

    private final String statement;

    private final int batchSize;

    private final List<List<Object>> parameterBuffer = new ArrayList<>();

    private final List<Future<?>> futureList = new ArrayList<>();

    private final Consumer<Long> onFlush;

    private final ExecutorService threadPool;

    private final AtomicLong counter = new AtomicLong();

    public BatchExecutor(Hybatis hybatis, String statement, int batchSize) {
        this(hybatis, statement, batchSize, null, null);
    }

    public BatchExecutor(Hybatis hybatis, String statement,
                         int batchSize, Consumer<Long> onFlush,
                         ExecutorService threadPool) {
        this.hybatis = hybatis;
        this.statement = statement;
        this.batchSize = batchSize;
        this.onFlush = onFlush;
        this.threadPool = threadPool;

        if (threadPool != null) {
            var autoClean = new Thread(() -> {
                while (true) {
                    synchronized (this.futureList) {
                        this.futureList.removeIf(Future::isDone);
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        // ignore this error
                    }
                }
            });
            autoClean.setDaemon(true);
            autoClean.start();
        }
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
        return counter.get();
    }

    public synchronized void resetCounter() {
        this.counter.set(0);
    }

    public synchronized void feed(List<Object> parameters) throws SQLException {
        this.parameterBuffer.add(parameters);
        if (this.parameterBuffer.size() >= this.batchSize) {
            flush();
        }
    }

    public synchronized void finish() throws SQLException {
        flush();
    }

    private synchronized void flush() throws SQLException {
        if (this.parameterBuffer.isEmpty()) {
            return;
        }

        SqlFunction function = () -> {
            this.hybatis.executeBatch(this.statement, this.parameterBuffer);
            this.counter.addAndGet(this.parameterBuffer.size());
            this.parameterBuffer.clear();
            if (onFlush != null) {
                onFlush.accept(this.counter.get());
            }
        };

        if (threadPool == null) {
            function.run();
        } else {
            Future<?> future = threadPool.submit(function.toRunnable());
            this.futureList.add(future);
        }
    }

    public synchronized void awaitCompletion() {
        if (threadPool == null) {
            return;
        }
        for (Future<?> future : this.futureList) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        this.futureList.clear();
    }
}
