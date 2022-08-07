package com.hyd.hybatis.page;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.annotations.HbPageQuery;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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

    }

    ///////////////////////////////////////////

    public static void parsePageParams(HybatisConfiguration conf, Object arg) {
        int pageSize, pageIndex;
        String pageSizeParamName, pageIndexParamName;
        var annotated = arg.getClass().isAnnotationPresent(HbPageQuery.class);

        if (annotated) {
            var hbPageQuery = arg.getClass().getAnnotation(HbPageQuery.class);

            if (hbPageQuery.pageIndexParamName().equals(DEFAULT_PAGE_INDEX_NAME)) {
                pageIndexParamName = conf.getPageIndexParamName();
            } else {
                pageIndexParamName = hbPageQuery.pageIndexParamName();
            }

            if (hbPageQuery.pageSizeParamName().equals(DEFAULT_PAGE_SIZE_NAME)) {
                pageSizeParamName = conf.getPageSizeParamName();
            } else {
                pageSizeParamName = hbPageQuery.pageSizeParamName();
            }
        } else {
            pageIndexParamName = conf.getPageIndexParamName();
            pageSizeParamName = conf.getPageSizeParamName();
        }

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

        // 'Annotated' means pagination parameters are optional.
        // When they are not present, use default values instead.
        if (annotated) {
            pageIndex = Math.max(pageIndex, DEFAULT_PAGE_INDEX);
            pageSize = Math.max(pageSize, DEFAULT_PAGE_SIZE);
        }

        var context = Context.getInstance();
        context.setPageSize(pageSize);
        context.setPageIndex(pageIndex);
    }

    private static int parseInt(String s) {
        if (!StringUtils.hasText(s)) {
            return 0;
        }
        return Integer.parseInt(s);
    }
}
