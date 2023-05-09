package com.hyd.hybatis.utils;

import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.reflection.Reflections;

public class MapperUtil {

    public static String[] primaryKeyNames(Class<?> type) {
        var a = Reflections.getAnnotationFromInterface(type, HbMapper.class);
        if (a != null && a.primaryKeyNames().length > 0) {
            return a.primaryKeyNames();
        }
        return new String[0];
    }
}
