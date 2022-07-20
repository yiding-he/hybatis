package com.hyd.hybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解实体类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface HbEntity {

    /**
     * 表示实体类对应数据库哪张表
     */
    String table();
}
