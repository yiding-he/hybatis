package com.hyd.hybatis.page;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.statement.MappedStatementFactories;
import com.hyd.hybatis.statement.msfactory.SelectMappedStatementFactory;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


public class Pagination {

    public static final int DEFAULT_PAGE_INDEX = 0;

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String DEFAULT_PAGE_INDEX_NAME = "pageIndex";

    public static final String DEFAULT_PAGE_SIZE_NAME = "pageSize";

    @Data
    public static class Context {

        private static final ThreadLocal<Context> instance = ThreadLocal.withInitial(Context::new);

        public static Context getInstance() {
            return instance.get();
        }

        public static void clear() {
            instance.remove();
        }

        private int totalRows;

        private int totalPages;

        private int pageIndex;

        private int pageSize;

        public void updateTotal(Long count) {
            this.totalRows = count.intValue();
            this.totalPages = this.pageSize == 0 ? 0 : ((this.totalRows + 1) / this.pageSize);
        }
    }

    ///////////////////////////////////////////

    private static final Map<Method, Boolean> checkResultCache = Collections.synchronizedMap(new WeakHashMap<>());

    public static Boolean isPaginationSelect(Method method, MappedStatementFactories factories) {
        var valid = checkResultCache.get(method);
        if (valid == null) {
            var factory = factories.getMappedStatementFactory(method);
            if (factory instanceof SelectMappedStatementFactory) {
                var hbSelect = method.getAnnotation(HbSelect.class);
                if (!hbSelect.pagination()) {
                    valid = false;
                } else {
                    if (SelectMappedStatementFactory.isCounting(method)) {
                        throw new IllegalStateException("Pagination cannot be applied to counting method " + method);
                    }
                    valid = true;
                }
            } else {
                valid = false;
            }
            checkResultCache.put(method, valid);
        }
        return valid;
    }


    public static void parsePageParams(Method method, HybatisConfiguration conf) {
        var pagination = method.getAnnotation(HbSelect.class).pagination();
        if (!pagination) {
            return;
        }

        var context = Context.getInstance();
        int pageSize = context.getPageSize(), pageIndex;

        if (pageSize > 0) {
            // Page params already set up, won't replace them with HTTP parameter values.
            return;
        }

        String pageIndexParamName = conf.getPageIndexParamName();
        String pageSizeParamName = conf.getPageSizeParamName();

        // Extract page size and page index parameter values
        var ra = RequestContextHolder.getRequestAttributes();
        if (ra instanceof ServletRequestAttributes) {
            var sra = (ServletRequestAttributes) ra;
            var request = sra.getRequest();
            pageSize = parseInt(request.getParameter(pageSizeParamName));
            pageIndex = parseInt(request.getParameter(pageIndexParamName));
        } else {
            pageSize = 0;
            pageIndex = 0;
        }

        // Pagination parameters are optional.
        // When they are not present, use default values instead.
        pageIndex = pageIndex <= 0? DEFAULT_PAGE_INDEX: pageIndex;
        pageSize = pageSize <= 0? DEFAULT_PAGE_SIZE: pageSize;

        context.setPageSize(pageSize);
        context.setPageIndex(pageIndex);
    }

    private static int parseInt(String s) {
        if (!StringUtils.hasText(s)) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    public static void setup(int pageSize, int pageIndex) {
        var c = Context.getInstance();
        c.setPageIndex(pageIndex);
        c.setPageSize(pageSize);
    }
}
