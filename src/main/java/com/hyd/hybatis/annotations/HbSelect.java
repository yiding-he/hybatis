package com.hyd.hybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解查询条件对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD
})
public @interface HbSelect {

    /**
     * 表示该查询是针对哪个表
     */
    String table();

    /**
     * 表示查询哪些字段
     */
    String[] fields() default {};
}
