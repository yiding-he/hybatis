package com.hyd.hybatis.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class Obj {

    /**
     * 判断一个对象是否不为空，对象可以是字符串、数组、集合或其他类型
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 判断一个对象是否为空，对象可以是字符串、数组、集合或其他类型
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof CharSequence) {
            return Str.isBlank((CharSequence) o);
        } else if (o instanceof Map) {
            return ((Map<?, ?>) o).isEmpty();
        } else if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        } else if (o.getClass().isArray()) {
            return Array.getLength(o) == 0;
        } else {
            return false;
        }
    }

    public static boolean isAnyEmpty(Object... os) {
        for (Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllEmpty(Object... os) {
        for (Object o : os) {
            if (isNotEmpty(o)) {
                return false;
            }
        }
        return true;
    }

    public static <T> T defaultValue(T value, T defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

}
