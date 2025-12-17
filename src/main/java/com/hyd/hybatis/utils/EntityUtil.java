package com.hyd.hybatis.utils;

import com.hyd.hybatis.HybatisException;
import com.hyd.hybatis.annotations.HbEntity;
import com.hyd.hybatis.query.Getter;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityUtil {

    public static String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(HbEntity.class)) {
            return entityClass.getAnnotation(HbEntity.class).table();
        }
        return Str.camel2Underline(entityClass.getSimpleName());
    }

    public static <T> String resolveGetter(Getter<T, ?> getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(getter);
            return Str.camel2Underline(
                Str.removeStart(serializedLambda.getImplMethodName(), "get", "is")
            );
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new HybatisException(e);
        }
    }
}
