package com.hyd.hybatis.reflection;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisException;
import com.hyd.hybatis.annotations.HbColumn;
import com.hyd.hybatis.utils.Bean;
import com.hyd.hybatis.utils.Str;
import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.annotation.Annotation;
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
        while (t != Object.class && t != null) {
            var declaredFields = t.getDeclaredFields();
            fieldList.addAll(
                Stream.of(declaredFields)
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
     * 判断 pojoType 是否属于查询条件。只要类中存在至少一个 Condition 类型的成员，
     * 或该类自身就是 Conditions 或其子类，就认为它是查询条件。
     */
    public static boolean isPojoClassQueryable(Class<?> pojoType) {
        return
            Conditions.class.isAssignableFrom(pojoType) ||
            !getPojoFieldsOfType(pojoType, Condition.class, Collections.emptyList()).isEmpty();
    }

    /**
     * 当方法的返回值类型是集合时，获取集合的元素类型；否则获取返回值类型本身
     */
    public static Class<?> getReturnEntityType(Method method) {
        var type = TypeParameterResolver.resolveReturnType(method, method.getDeclaringClass());
        return getGenericTypeArg(type);
    }

    public static Class<?> getGenericTypeArg(Type type) {
        if (type instanceof Class && ((Class<?>) type).getGenericInterfaces().length > 0) {
            if (((Class<?>) type).getGenericInterfaces()[0] instanceof ParameterizedType) {
                var genericInterface = (ParameterizedType) ((Class<?>) type).getGenericInterfaces()[0];
                var arg0 = genericInterface.getActualTypeArguments()[0];
                return arg0 instanceof Class? (Class<?>) arg0: null;
            } else {
                return null;
            }
        } else if (type instanceof ParameterizedType) {
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

    public static <T> T getFieldValue(Object parameterObject, Field field) {
        return getFieldValue(parameterObject, field, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object parameterObject, Field field, Class<T> expectedType) {
        try {
            T result = null;
            if (Modifier.isPublic(field.getModifiers())) {
                result = (T) field.get(parameterObject);
            } else {
                var fieldName = field.getName();
                var fieldType = field.getType();
                var getterPrefix = (fieldType == boolean.class) ? "is" : "get";
                var getterName = getterPrefix + Str.capitalize(fieldName);
                var getterMethod = field.getDeclaringClass().getMethod(getterName);
                if (getterMethod.canAccess(parameterObject)) {
                    var fieldValue = getterMethod.invoke(parameterObject);
                    result = (T) fieldValue;
                }
            }

            if (result != null && expectedType != null && !expectedType.isAssignableFrom(result.getClass())) {
                result = null;
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setFieldValue(Object parameterObject, Field field, Object fieldValue) {
        try {
            if (Modifier.isPublic(field.getModifiers())) {
                field.set(parameterObject, fieldValue);
                return;
            }

            var fieldName = field.getName();
            var fieldType = field.getType();
            fieldValue = Bean.convertValue(fieldValue, fieldType);

            var setterName = "set" + Str.capitalize(fieldName);
            var setterMethod = field.getDeclaringClass().getMethod(setterName, fieldType);
            if (setterMethod.canAccess(parameterObject)) {
                setterMethod.invoke(parameterObject, fieldValue);
            }
        } catch (Exception e) {
            throw new HybatisException(e);
        }
    }

    public static String getColumnName(Field field, boolean camelToUnderline) {
        if (field.isAnnotationPresent(HbColumn.class)) {
            return field.getAnnotation(HbColumn.class).value();
        } else {
            return camelToUnderline? Str.camel2Underline(field.getName()): field.getName();
        }
    }

    /**
     * 判断一个方法是否包含执行内容
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasBody(Method method) {
        return Modifier.isStatic(method.getModifiers())
            || method.isDefault()
            || !Modifier.isAbstract(method.getModifiers());
    }

    /**
     * 查找 Mapper 接口及其父接口中定义的非 default 方法
     */
    public static List<Method> getMapperNonDefaultMethods(Class<?> mapperClass) {
        ArrayList<Method> nonDefaultMethods = new ArrayList<>();
        if (!mapperClass.isInterface()) {
            return nonDefaultMethods;
        }

        scanInterfaceAndAddMethod(mapperClass, nonDefaultMethods);
        return nonDefaultMethods;
    }

    private static void scanInterfaceAndAddMethod(
        Class<?> interfaceClass, ArrayList<Method> nonDefaultMethods
    ) {
        Stream.of(interfaceClass.getDeclaredMethods())
            .filter(m -> !hasBody(m))
            .forEach(nonDefaultMethods::add);

        Stream.of(interfaceClass.getInterfaces())
            .forEach(i -> scanInterfaceAndAddMethod(i, nonDefaultMethods));
    }

    ////////////////////////////////////////

    /**
     * Recursively enumerate all interfaces implemented by {@code type}.
     */
    public static List<Class<?>> allInterfaces(Class<?> type) {
        var list = new ArrayList<Class<?>>();
        allInterfaces(type, list);
        return list;
    }

    private static void allInterfaces(Class<?> type, ArrayList<Class<?>> collector) {
        if (type.isInterface()) {
            collector.add(type);
        }
        Collections.addAll(collector, type.getInterfaces());
    }

    public static <T extends Annotation> T getAnnotationFromInterface(Class<?> type, Class<T> annotationType) {
        return allInterfaces(type).stream()
            .filter(i -> i.isAnnotationPresent(annotationType))
            .map(i -> i.getAnnotation(annotationType))
            .findFirst().orElse(null);
    }
}
