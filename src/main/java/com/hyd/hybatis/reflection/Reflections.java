package com.hyd.hybatis.reflection;

import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflections {

    /**
     * 查询 pojoType 中所有属于 fieldType 类型的成员
     */
    public static List<Field> getPojoFieldsOfType(Class<?> pojoType, Class<?> fieldType) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> t = pojoType;
        while (t != Object.class) {
            fieldList.addAll(
                Stream.of(t.getDeclaredFields())
                    .filter(f -> fieldType.isAssignableFrom(f.getType()))
                    .collect(Collectors.toList())
            );
            t = t.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 当方法的返回值类型是集合时，获取集合的元素类型；否则获取返回值类型本身
     */
    public static Class<?> getReturnEntityType(Method method) {
        var type = TypeParameterResolver.resolveReturnType(method, method.getDeclaringClass());
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        } else {
            return (Class<?>) type;
        }
    }

}
