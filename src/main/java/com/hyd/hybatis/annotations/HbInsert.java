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
public @interface HbInsert {

    String table();

    /**
     * 遇到主键冲突是否忽略，对应 "INSERT IGNORE INTO..." 语法，仅部分数据库支持
     */
    boolean onDuplicateKeyIgnore() default false;

    /**
     * 遇到主键冲突时，是否改为更新指定的字段。仅部分数据库支持
     */
    String[] onDuplicateKeyUpdate() default {};
}
