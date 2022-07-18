package com.hyd.hybatis.reflection;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbColumn;
import com.hyd.hybatis.utils.Str;
import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflections {

    /**
     * 查询 pojoType 中所有属于 fieldType 类型的成员
     */
    public static List<Field> getPojoFields(Class<?> pojoType, List<Class<?>> hideBeanFieldsFrom) {
        return getPojoFieldsOfType(pojoType, null, hideBeanFieldsFrom);
    }

    /**
     * 查询 pojoType 中所有属于 fieldType 类型的成员
     */
    public static List<Field> getPojoFieldsOfType(
        Class<?> pojoType, Class<?> fieldType, List<Class<?>> hideBeanFieldsFrom
    ) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> t = pojoType;
        while (t != Object.class) {
            fieldList.addAll(
                Stream.of(t.getDeclaredFields())
                    .filter(f -> fieldType == null || fieldType.isAssignableFrom(f.getType()))
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .filter(f -> !isTypeToBeIgnored(hideBeanFieldsFrom, f))
                    .collect(Collectors.toList())
            );
            t = t.getSuperclass();
        }
        return fieldList;
    }

    private static boolean isTypeToBeIgnored(List<Class<?>> hideBeanFieldsFrom, Field f) {
        return hideBeanFieldsFrom.contains(f.getDeclaringClass());
    }

    /**
     * 判断指定的类是否属于查询条件。只要类中存在至少一个 Condition 类型的成员，或该类自身就是 Condition
     * 就认为它是查询条件。
     */
    public static boolean isPojoClassQueryable(Class<?> pojoType) {
        return
            Conditions.class.isAssignableFrom(pojoType) ||
                Condition.class.isAssignableFrom(pojoType) ||
                getPojoFieldsOfType(pojoType, Condition.class, Collections.emptyList()).size() > 0;
    }

    /**
     * 当方法的返回值类型是集合时，获取集合的元素类型；否则获取返回值类型本身
     */
    public static Class<?> getReturnEntityType(Method method) {
        var type = TypeParameterResolver.resolveReturnType(method, method.getDeclaringClass());
        return getGenericTypeArg(type);
    }

    public static Class<?> getGenericTypeArg(Type type) {
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            Type args0 = parameterizedType.getActualTypeArguments()[0];
            if (args0 instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) args0).getRawType();
            } else {
                return (Class<?>) args0;
            }
        } else {
            return (Class<?>) type;
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object parameterObject, Field field) {
        try {
            if (Modifier.isPublic(field.getModifiers())) {
                return (T) field.get(parameterObject);
            }

            var fieldName = field.getName();
            Class<?> fieldType = field.getType();
            var getterPrefix = (fieldType == boolean.class) ? "is" : "get";
            var getterName = getterPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            var getterMethod = field.getDeclaringClass().getMethod(getterName);
            if (Modifier.isPublic(getterMethod.getModifiers())) {
                var fieldValue = getterMethod.invoke(parameterObject);
                return (T) fieldValue;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(HbColumn.class)) {
            return field.getAnnotation(HbColumn.class).value();
        } else {
            return Str.camel2Underline(field.getName());
        }
    }

    /**
     * 判断一个方法是否包含执行内容
     */
    public static boolean hasBody(Method method) {
        return Modifier.isStatic(method.getModifiers())
            || method.isDefault()
            || !Modifier.isAbstract(method.getModifiers());
    }
}
