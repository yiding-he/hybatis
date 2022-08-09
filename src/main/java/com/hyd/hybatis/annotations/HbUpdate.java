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
    ElementType.TYPE,
    ElementType.METHOD
})
public @interface HbUpdate {

    /**
     * 表示该查询是针对哪个表
     */
    String table();

    /**
     * 表示表的主键是哪几个列，当 Mapper 方法第一个参数是查询参数时，忽略本属性
     */
    String[] key() default {};
}
