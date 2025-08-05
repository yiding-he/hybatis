package com.hyd.hybatis.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解要插入的对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HbDelete {

    /**
     * 表示本方法针对的是哪张表。如果为空，则框架会从 Mapper 类那里取
     */
    String table() default "";
}
