package com.hyd.hybatis.query;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * 查询上下文追踪器，用于检测循环引用
 * 使用线程本地变量确保线程安全
 * 
 * 提供两种使用方式：
 * 1. 传统 try-finally 方式：pushQuery() / popQuery()
 * 2. 函数式接口方式：withQuery()
 */
public class QueryContextTracker {
    
    private static final ThreadLocal<Deque<Query>> QUERY_STACK = ThreadLocal.withInitial(ArrayDeque::new);
    
    /**
     * 开始处理一个查询，将其推入堆栈
     * @param query 要处理的查询对象
     * @throws IllegalStateException 如果检测到循环引用
     */
    public static void pushQuery(Query query) {
        Deque<Query> stack = QUERY_STACK.get();
        
        // 检查循环引用
        if (stack.contains(query)) {
            throw new IllegalStateException(
                "Detected circular reference in query: " + query.getClass().getSimpleName() + 
                ". This would cause infinite recursion when generating SQL. " +
                "Make sure you are not using the same query object in both outer query and EXISTS subquery."
            );
        }
        
        stack.push(query);
    }
    
    /**
     * 结束处理一个查询，将其从堆栈中移除
     */
    public static void popQuery() {
        Deque<Query> stack = QUERY_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }
    
    /**
     * 在指定查询的上下文中执行操作，自动管理堆栈
     * 这是推荐的使用方式，避免了手动的 try-finally 结构
     * 
     * @param query 要处理的查询对象
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 操作的返回值
     * @throws IllegalStateException 如果检测到循环引用
     */
    public static <T> T withQuery(Query query, Supplier<T> action) {
        pushQuery(query);
        try {
            return action.get();
        } finally {
            popQuery();
        }
    }
    
    /**
     * 在指定查询的上下文中执行操作，无返回值版本
     * 
     * @param query 要处理的查询对象
     * @param action 要执行的操作
     * @throws IllegalStateException 如果检测到循环引用
     */
    public static void withQuery(Query query, Runnable action) {
        pushQuery(query);
        try {
            action.run();
        } finally {
            popQuery();
        }
    }
    
    /**
     * 获取当前堆栈深度（用于调试）
     */
    public static int getDepth() {
        return QUERY_STACK.get().size();
    }
    
    /**
     * 清空当前线程的堆栈（通常在测试完成后调用）
     */
    public static void clear() {
        QUERY_STACK.get().clear();
    }
}