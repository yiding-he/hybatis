package com.hyd.hybatis.interceptor;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.page.Pagination;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import com.hyd.hybatis.statement.MappedStatementHelper;
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

import java.util.Collections;

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

        // TODO 这里搞错了，invocation.getMethod() 拿到的不是 Mapper 方法
        Boolean isPaginationSelect = Pagination.isPaginationSelect(
            invocation.getMethod(), core.getMappedStatementFactories()
        );

        if (isPaginationSelect) {
            // Do a counting query if necessary
            processPagination(invocation);
        }

        return invocation.proceed();
    }

    private void processPagination(Invocation invocation) {
        var method = invocation.getMethod();

        // TODO 这个时候才判断是否是分页查询，可能已经太晚了，因为 BoundSql 已经生成
        Pagination.parsePageParams(method, core.getConf());
        var context = Pagination.Context.getInstance();
        int pageSize = context.getPageSize();
        int pageIndex = context.getPageIndex();

        if (pageSize <= 0) {
            return;
        }

        var executor = (Executor) invocation.getTarget();
        var ms = (MappedStatement) invocation.getArgs()[0];
        var sqlSource = ms.getSqlSource();

        if (sqlSource instanceof SqlSourceForSelect) {
            var countingSqlSource = ((SqlSourceForSelect) sqlSource).newCountingSqlSource();

            var resultMap = new ResultMap
                .Builder(ms.getConfiguration(), ms.getId(), Long.class, Collections.emptyList())
                .build();

            var countingMs = MappedStatementHelper
                .cloneWithNewSqlSourceAndResultMap(ms, countingSqlSource, resultMap, "_cnt");

            Pagination.Context.getInstance().updateTotal(
                executeCount(executor, countingMs, invocation.getArgs())
            );
        }
    }

    private Long executeCount(Executor executor, MappedStatement countingMs, Object[] invocationArgs) {
        // TODO 执行 count 查询
        return 0L;
    }

}
