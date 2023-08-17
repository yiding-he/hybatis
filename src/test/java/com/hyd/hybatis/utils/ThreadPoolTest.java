package com.hyd.hybatis.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ThreadPoolTest {

    private ExecutorService threadPool;

    public ThreadPoolTest(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public void submit(Runnable task) {
        Future<?> future = this.threadPool.submit(task);
    }

    public synchronized void awaitCompletion() {
        // TODO 等待所有被 submit 方法提交的任务全部完成再返回
    }
}
