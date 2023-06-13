package com.hyd.hybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A supplementary annotation for CrudMapper sub interfaces.
 * <p>
 * When the table name cannot be inferred from the entity class
 * corresponding to the CrudMapper interface, this annotation is
 * used to specify which database table this CrudMapper corresponds to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface HbMapper {

    /**
     * Indicates which table the CrudMapper interface corresponds to
     */
    String table();

    /**
     * Name of the primary keys of the table the CrudMapper interface corresponds to.
     */
    String[] primaryKeyNames() default {};
}
