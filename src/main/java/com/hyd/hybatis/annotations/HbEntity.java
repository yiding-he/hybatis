package com.hyd.hybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate entity classes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface HbEntity {

    /**
     * Indicates which table the entity class corresponds to in the database
     */
    String table();

    /**
     * Name of the primary keys of the table the CrudMapper interface corresponds to.
     */
    String[] primaryKeyNames() default {};
}
