package com.hyd.hybatis.annotations;

import com.hyd.hybatis.page.Pagination;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加在查询 Bean 上的注解，表示处理请求时要检查分页参数，
 * 如果参数存在且不为零，则进行分页查询
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface HbPageQuery {

    /**
     * 默认的页号参数名。
     * 如果在配置中指定了另外的名字，而这里使用的还是默认名字，则以配置为准；
     * 如果在这里指定了另外的名字，则以这里为准。
     */
    String pageIndexParamName() default Pagination.DEFAULT_PAGE_INDEX_NAME;

    /**
     * 默认的页大小参数名。
     * 如果在配置中指定了另外的名字，而这里使用的还是默认名字，则以配置为准；
     * 如果在这里指定了另外的名字，则以这里为准。
     */
    String pageSizeParamName() default Pagination.DEFAULT_PAGE_SIZE_NAME;
}
