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
import java.util.*;

/**
 * 处理 bean 对象的帮助类
 */
@SuppressWarnings("unchecked")
public class Bean {

    static Map<Class, Class> primitiveToWrapper = new HashMap<>();

    static Map<Class, Class> wrapperToPrimitive = new HashMap<>();

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

    public static Class getPrimitive(Class wrapperClass) {
        return wrapperToPrimitive.get(wrapperClass);
    }

    public static Class getWrapper(Class primitiveClass) {
        return primitiveToWrapper.get(primitiveClass);
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
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj.getClass(), fieldName);
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
    private static Method getPropertyMethod(Class clazz, String fieldName, boolean getter) {
        PropertyDescriptor descriptor = getPropertyDescriptor(clazz, fieldName);
        return descriptor == null ? null :
            getter ? descriptor.getReadMethod() : descriptor.getWriteMethod();
    }

    private static PropertyDescriptor getPropertyDescriptor(Class clazz, String fieldName) {

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
     * 将一个值转化为指定的属性类型，以便于赋到对象属性。注意，当 value 的值超过属性类型允许的最大值时，强制转换将起作用。
     * <p>
     * 本方法主要处理数字类型的查询结果，对于字符类型和日期类型则不作处理，直接赋值。
     *
     * @param value 值
     * @param clazz 属性类型
     *
     * @return 转化后的属性值
     *
     * @throws NoSuchMethodException  如果 field 指名的类不包含一个以字符串为参数的构造函数
     * @throws IllegalAccessException 如果执行构造函数失败
     * @throws InstantiationException 如果执行构造函数失败
     */
    private static Object convertValue0(Object value, Class<?> clazz)
        throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (value == null) {
            return null;
        }

        // 如果类型刚好相符就直接返回 value
        if (value.getClass() == clazz ||
            (clazz.isPrimitive() && getWrapper(clazz) == value.getClass())) {
            return value;
        }

        if (clazz == String.class) {
            return String.valueOf(value);

        } else if (clazz == Integer.class || clazz == Long.class || clazz == Double.class ||
            clazz == BigDecimal.class || clazz == BigInteger.class) {
            try {
                String str_value = new BigDecimal(String.valueOf(value)).toPlainString();

                // 避免因为带有小数点而无法转换成 Integer/Long。
                // 但如果值真的带小数，那么为了避免精度丢失，只有抛出异常了。
                if (str_value.endsWith(".0")) {
                    str_value = str_value.substring(0, str_value.length() - 2);
                }

                return clazz.getDeclaredConstructor(String.class).newInstance(str_value);

            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof NumberFormatException) {
                    String message = "Value " + value + " (" + value.getClass() + ") cannot convert to " + clazz;
                    throw new HybatisException(message, e);
                }
            }
        } else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
            return Boolean.valueOf(String.valueOf(value));

        } else if (clazz.isPrimitive()) { // 处理基本型别

            BigDecimal bdValue = new BigDecimal(String.valueOf(value));

            if (clazz == Integer.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for integer");
                }
                return bdValue.intValue();

            } else if (clazz == Long.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for long");
                }
                return bdValue.longValue();

            } else if (clazz == Double.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for double");
                }
                return bdValue.doubleValue();

            } else if (clazz == Byte.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Byte.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for byte");
                }
                return bdValue.byteValue();

            } else if (clazz == Short.TYPE) {

                if (bdValue.compareTo(new BigDecimal(Short.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for short");
                }
                return bdValue.shortValue();

            } else if (clazz == Float.TYPE) {

                if (bdValue.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
                    throw new HybatisException("Value " + bdValue + " is too large for float");
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
