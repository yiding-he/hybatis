package com.hyd.hybatis.utils;

import com.hyd.hybatis.HybatisException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理 bean 对象的帮助类
 */
@SuppressWarnings("unchecked")
public class Bean {

    static Map<Class<?>, Class<?>> primitiveToWrapper = new HashMap<>();

    static Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<>();

    static {
        primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
        primitiveToWrapper.put(Byte.TYPE, Byte.class);
        primitiveToWrapper.put(Short.TYPE, Short.class);
        primitiveToWrapper.put(Character.TYPE, Character.class);
        primitiveToWrapper.put(Integer.TYPE, Integer.class);
        primitiveToWrapper.put(Long.TYPE, Long.class);
        primitiveToWrapper.put(Float.TYPE, Float.class);
        primitiveToWrapper.put(Double.TYPE, Double.class);
        wrapperToPrimitive.put(Boolean.class, Boolean.TYPE);
        wrapperToPrimitive.put(Byte.class, Byte.TYPE);
        wrapperToPrimitive.put(Short.class, Short.TYPE);
        wrapperToPrimitive.put(Character.class, Character.TYPE);
        wrapperToPrimitive.put(Integer.class, Integer.TYPE);
        wrapperToPrimitive.put(Long.class, Long.TYPE);
        wrapperToPrimitive.put(Float.class, Float.TYPE);
        wrapperToPrimitive.put(Double.class, Double.TYPE);
    }

    public static Class<?> primitiveTypeOf(Class<?> wrapperClass) {
        return wrapperToPrimitive.get(wrapperClass);
    }

    public static Class<?> wrapperTypeOf(Class<?> primitiveClass) {
        return primitiveToWrapper.get(primitiveClass);
    }

    /**
     * Determine if srcType is equal to targetType or a subclass of targetType.
     */
    public static boolean matchType(Class<?> srcType, Class<?> targetType) {
        if (targetType.isAssignableFrom(srcType) || srcType.equals(targetType)) {
            return true;
        }
        if (srcType.isPrimitive() && !targetType.isPrimitive()) {
            return matchType(wrapperTypeOf(srcType), targetType);
        } else if (!srcType.isPrimitive() && targetType.isPrimitive()) {
            return matchType(srcType, wrapperTypeOf(targetType));
        } else {
            return false;
        }
    }

    private Bean() {

    }

    /**
     * 设置一个对象的属性
     *
     * @param obj       要设置的对象
     * @param fieldName 属性名（忽略大小写）
     * @param value     值
     */
    public static void setValueIgnoreCase(final Object obj, final String fieldName, final Object value) {
        if (value == null) {
            return;
        }

        try {
            PropertyDescriptor propertyDescriptor = findProperty(obj.getClass(), fieldName);
            if (propertyDescriptor == null) {
                throw new HybatisException(
                    "Field not found for " + obj.getClass().getCanonicalName() + "#" + fieldName);
            }

            Class<?> fieldType = propertyDescriptor.getPropertyType();
            Object convertedValue = convertValue0(value, fieldType);

            Method writeMethod = getPropertyMethod(obj.getClass(), fieldName, false);
            if (writeMethod == null) {
                throw new HybatisException(
                    "Missing write method for " + obj.getClass().getCanonicalName() + "#" + fieldName);
            }

            writeMethod.invoke(obj, convertedValue);

        } catch (HybatisException e) {
            throw e;
        } catch (Exception e) {
            throw new HybatisException("Cannot set property " + obj.getClass().getCanonicalName() + "#" + fieldName, e);
        }
    }

    /**
     * 获得一个属性的 getter 或 setter 方法
     *
     * @param clazz     包含属性的类
     * @param fieldName 属性名
     * @param getter    是否取 getter 方法。如果是 false，则表示取 setter 方法。
     *
     * @return 方法对象
     */
    private static Method getPropertyMethod(Class<?> clazz, String fieldName, boolean getter) {
        PropertyDescriptor descriptor = findProperty(clazz, fieldName);
        return descriptor == null ? null :
            getter ? descriptor.getReadMethod() : descriptor.getWriteMethod();
    }

    private static PropertyDescriptor findProperty(Class<?> clazz, String fieldName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getName().equalsIgnoreCase(fieldName)) {
                    return descriptor;
                }
            }

            return null;
        } catch (IntrospectionException e) {
            throw new HybatisException("Error parsing class '" + clazz.getCanonicalName() + "'", e);
        }
    }

    public static <T> T convertValue(Object value, Class<T> clazz) {
        try {
            return (T) convertValue0(value, clazz);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new HybatisException(e);
        }
    }

    /**
     * <p>Converts a value to the specified target type in order to assign it to an object property.</p>
     * <p>When the value exceeds the maximum allowed value of the target type, an exception will be thrown.</p>
     * <p>This method is mainly used to handle numeric query results,
     * and does not perform any conversion for character types and date types.</p>
     *
     * @param value      the value to be converted
     * @param targetType the target property type
     *
     * @return the converted property value
     *
     * @throws NoSuchMethodException  if the class of the field does not have a string argument constructor
     * @throws IllegalAccessException if the constructor execution fails
     * @throws InstantiationException if the constructor execution fails
     */
    private static Object convertValue0(Object value, Class<?> targetType)
        throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (value == null) {
            return null;
        }

        if (matchType(value.getClass(), targetType)) {
            return value;
        }

        if (targetType == String.class) {
            return String.valueOf(value);

        } else if (targetType == Integer.class || targetType == Long.class || targetType == Double.class ||
                   targetType == BigDecimal.class || targetType == BigInteger.class) {
            try {
                String str_value = new BigDecimal(String.valueOf(value)).toPlainString();

                // 避免 str_value 因为带有小数点而无法转换成 Integer/Long。
                // 但如果值真的带小数，那么为了避免精度丢失，只有抛出异常了。
                if (str_value.endsWith(".0")) {
                    str_value = str_value.substring(0, str_value.length() - 2);
                }

                return targetType.getDeclaredConstructor(String.class).newInstance(str_value);

            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof NumberFormatException) {
                    String message = "Value " + value + " (" + value.getClass() + ") cannot convert to " + targetType;
                    throw new HybatisException(message, e);
                }
            }
        } else if (targetType == Boolean.TYPE || targetType == Boolean.class) {
            return Boolean.valueOf(String.valueOf(value));

        } else if (targetType.isPrimitive()) { // 处理基本型别

            BigDecimal bdValue = new BigDecimal(String.valueOf(value));

            if (targetType == Integer.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0
                    || bdValue.compareTo(new BigDecimal(Integer.MIN_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for integer");
                }
                return bdValue.intValue();

            } else if (targetType == Long.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0
                    || bdValue.compareTo(new BigDecimal(Long.MIN_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for long");
                }
                return bdValue.longValue();

            } else if (targetType == Double.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0
                    || bdValue.compareTo(BigDecimal.valueOf(-Double.MAX_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for double");
                }
                return bdValue.doubleValue();

            } else if (targetType == Byte.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Byte.MAX_VALUE)) > 0
                    || bdValue.compareTo(new BigDecimal(Byte.MIN_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for byte");
                }
                return bdValue.byteValue();

            } else if (targetType == Short.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Short.MAX_VALUE)) > 0
                    || bdValue.compareTo(new BigDecimal(Short.MIN_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for short");
                }
                return bdValue.shortValue();

            } else if (targetType == Float.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0
                    || bdValue.compareTo(BigDecimal.valueOf(-Float.MAX_VALUE)) < 0) {
                    throw new HybatisException("Value " + bdValue + " is out of range for float");
                }
                return bdValue.floatValue();
            }
        }
        return value;
    }


    /**
     * 获得一个对象的属性
     *
     * @param obj       要获取的对象
     * @param fieldName 属性名
     *
     * @return 值。如果对象中没有该属性或该属性不可读，则返回null
     */
    public static Object getValue(Object obj, String fieldName) {
        Method getter = null;
        try {
            getter = getPropertyMethod(obj.getClass(), fieldName, true);
            return getter == null ? null : getter.invoke(obj);
        } catch (Exception e) {
            if (getter == null) {
                throw new HybatisException(
                    "Error getting property " + obj.getClass().getCanonicalName() + "#" + fieldName, e);
            } else {
                throw new HybatisException(
                    "Error executing method " + obj.getClass().getCanonicalName() + "#" +
                    getter.getName() + " " + modifiers(getter.getModifiers()), e);
            }
        }
    }

    public static List<String> modifiers(int modifiers) {
        List<String> result = new ArrayList<>();
        if (Modifier.isStatic(modifiers)) {
            result.add("static");
        }
        if (Modifier.isAbstract(modifiers)) {
            result.add("abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            result.add("final");
        }
        if (Modifier.isPrivate(modifiers)) {
            result.add("private");
        }
        if (Modifier.isProtected(modifiers)) {
            result.add("protected");
        }
        if (Modifier.isPublic(modifiers)) {
            result.add("public");
        }
        if (Modifier.isSynchronized(modifiers)) {
            result.add("synchronized");
        }
        return result;
    }

}
