package com.hyd.hybatis.interceptor;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.page.Pagination;
import com.hyd.hybatis.statement.msfactory.SelectMappedStatementFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class HybatisPageInterceptor implements Interceptor {

    private final HybatisCore core;

    private final Map<Method, Boolean> checkResultCache = Collections.synchronizedMap(new WeakHashMap<>());

    public HybatisPageInterceptor(HybatisCore core) {
        this.core = core;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Boolean isPaginationSelect = checkWithCache(invocation);
        if (isPaginationSelect) {
            // Do a counting query if necessary
            processPagination(invocation);
        }

        return invocation.proceed();
    }

    private Boolean checkWithCache(Invocation invocation) {
        var method = invocation.getMethod();
        var valid = checkResultCache.get(method);
        if (valid == null) {
            valid = isPaginationSelect(method);
            checkResultCache.put(method, valid);
        }
        return valid;
    }

    private boolean isPaginationSelect(Method method) {
        var factory =
            core.getMappedStatementFactories().getMappedStatementFactory(method);

        return factory instanceof SelectMappedStatementFactory
            && !SelectMappedStatementFactory.isCounting(method);
    }

    private void processPagination(Invocation invocation) {
        var arg = invocation.getArgs()[1];

        Pagination.parsePageParams(core.getConf(), arg);
        var context = Pagination.Context.getInstance();
        int pageSize = context.getPageSize();
        int pageIndex = context.getPageIndex();

        if (pageSize <= 0) {
            return;
        }

        // TODO execute counting query
    }

}
