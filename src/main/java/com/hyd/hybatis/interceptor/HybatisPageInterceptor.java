package com.hyd.hybatis.interceptor;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.page.Pagination;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import com.hyd.hybatis.statement.MappedStatementHelper;
import com.hyd.hybatis.utils.Bean;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class HybatisPageInterceptor implements Interceptor {

    private final HybatisCore core;

    public HybatisPageInterceptor(HybatisCore core) {
        this.core = core;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return intercept0(invocation);
    }

    private Object intercept0(Invocation invocation) throws SQLException, InvocationTargetException, IllegalAccessException, CloneNotSupportedException {
        var ms = (MappedStatement) invocation.getArgs()[0];
        var ss = ms.getSqlSource();

        boolean isPaginationSelect;
        Method mapperMethod = null;

        if (!(ss instanceof SqlSourceForSelect)) {
            isPaginationSelect = false;
        } else {
            mapperMethod = ((SqlSourceForSelect) ss).getMapperMethod();
            isPaginationSelect = Pagination.isPaginationSelect(mapperMethod, core.getMappedStatementFactories());
        }

        if (isPaginationSelect) {
            // Do a counting query if necessary
            return processPagination(invocation, mapperMethod);
        } else {
            return invocation.proceed();
        }
    }

    private Object processPagination(Invocation invocation, Method mapperMethod)
        throws SQLException, InvocationTargetException, IllegalAccessException, CloneNotSupportedException {

        // 解析分页参数并存储在 Pagination.Context 实例中
        Pagination.parsePageParams(mapperMethod, core.getConf());

        var executor = (Executor) invocation.getTarget();
        var context = Pagination.Context.getInstance();
        int pageSize = context.getPageSize();

        // 如果 pageSize 不正确，仍然不看作是分页查询
        if (pageSize <= 0) {
            return invocation.proceed();
        }

        ////////////////////////// 1. 构建并执行 count 查询
        var totalCount = executeQueryCount(executor, copyOfArgs(invocation));
        Pagination.Context.getInstance().updateTotal(totalCount);

        ////////////////////////// 2. 构建并执行 limit 查询
        return executeQueryItems(executor, copyOfArgs(invocation));
    }

    private static Object[] copyOfArgs(Invocation invocation) {
        return Arrays.copyOf(invocation.getArgs(), invocation.getArgs().length);
    }

    private Long executeQueryCount(Executor executor, Object[] args) throws SQLException, CloneNotSupportedException {

        var ms = (MappedStatement) args[0];
        var sqlSource = (SqlSourceForSelect) ms.getSqlSource();
        var countingSqlSource = sqlSource.asPaginationCountSqlSource();

        var resultMap = new ResultMap
            .Builder(ms.getConfiguration(), ms.getId() + "-cnt", Long.class, Collections.emptyList())
            .build();

        args[0] = MappedStatementHelper.cloneWithNewSqlSourceAndResultMap(ms, countingSqlSource, resultMap, "-cnt");
        List<?> queryResult = invokeExecutor(executor, args);

        return queryResult.isEmpty()? 0L: Bean.convertValue(queryResult.get(0), Long.class);
    }

    private List<?> executeQueryItems(Executor executor, Object[] args) throws SQLException, CloneNotSupportedException {

        var ms = (MappedStatement) args[0];
        var sqlSource = (SqlSourceForSelect) ms.getSqlSource();
        var itemsSqlSource = sqlSource.asPaginationItemsSqlSource();

        args[0] = MappedStatementHelper.cloneWithNewSqlSource(ms, itemsSqlSource, "-items");
        return invokeExecutor(executor, args);
    }

    private List<?> invokeExecutor(Executor executor, Object[] args) throws SQLException, CloneNotSupportedException {
        if (args.length == 4) {
            return executor.query(
                (MappedStatement) args[0], args[1], (RowBounds) args[2], (ResultHandler<?>) args[3]
            );
        } else if (args.length == 6) {
            args[4] = newCacheKey((CacheKey) args[4], ((MappedStatement) args[0]).getId());
            args[5] = ((MappedStatement) args[0]).getBoundSql(args[1]);
            return executor.query(
                (MappedStatement) args[0], args[1], (RowBounds) args[2], (ResultHandler<?>) args[3],
                (CacheKey) args[4], (BoundSql) args[5]
            );
        } else {
            throw new IllegalStateException("Shouldn't be here");
        }
    }

    private CacheKey newCacheKey(
        CacheKey cacheKey, String mappedStatementId
    ) throws CloneNotSupportedException {
        CacheKey result = cacheKey.clone();
        result.update(mappedStatementId);
        return result;
    }

}
