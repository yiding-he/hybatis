package com.hyd.hybatis.utils;

import com.hyd.hybatis.annotations.HbEntity;
import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.reflection.Reflections;

public class MapperUtil {

    /**
     * Try to get primary key information from the HbMapper annotation.
     */
    public static String[] primaryKeyNames(Class<?> type) {
        var a = Reflections.getAnnotationFromInterface(type, HbMapper.class);
        if (a != null && a.primaryKeyNames().length > 0) {
            return a.primaryKeyNames();
        }
        return new String[0];
    }

    /**
     * Try to get table name information from the HbMapper annotation of mapperClass,
     * or from the HbEntity annotation of its generic parameter entity class.
     */
    public static String getCrudMapperTableName(Class<?> mapperClass) {
        var allInterfaces = Reflections.allInterfaces(mapperClass);
        for (Class<?> i : allInterfaces) {
            String tableName = "";
            var entityClass = Reflections.getGenericTypeArg(i);
            if (entityClass != null && entityClass.isAnnotationPresent(HbEntity.class)) {
                tableName = entityClass.getAnnotation(HbEntity.class).table();
            }
            if (Str.isBlank(tableName) && i.isAnnotationPresent(HbMapper.class)) {
                tableName = i.getAnnotation(HbMapper.class).table();
            }
            if (Str.isNotBlank(tableName)) {
                return tableName;
            }
        }

        return "";
    }
}
