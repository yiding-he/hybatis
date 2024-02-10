package com.hyd.hybatis.utils;

import com.hyd.hybatis.annotations.HbEntity;
import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.reflection.Reflections;

public class MapperUtil {

    /**
     * Try to get primary key information from the HbMapper annotation of mapperClass,
     * or from the HbEntity annotation of its generic parameter entity class.
     */
    public static String[] getPrimaryKeys(Class<?> type) {
        var allInterfaces = Reflections.allInterfaces(type);
        for (Class<?> i : allInterfaces) {
            var a = Reflections.getAnnotationFromInterface(i, HbMapper.class);
            if (a != null && a.primaryKeyNames().length > 0) {
                return a.primaryKeyNames();
            }

            Class<?> entityType = Reflections.getGenericTypeArg(i);
            if (entityType == null || !entityType.isAnnotationPresent(HbEntity.class)) {
                continue;
            }
            return entityType.getAnnotation(HbEntity.class).primaryKeyNames();
        }
        return new String[0];
    }

    /**
     * Try to get table name information from the HbMapper annotation of mapperClass,
     * or from the HbEntity annotation of its generic parameter entity class.
     */
    public static String getTableName(Class<?> mapperClass) {
        var allInterfaces = Reflections.allInterfaces(mapperClass);
        for (Class<?> i : allInterfaces) {
            String tableName = "";
            var entityType = Reflections.getGenericTypeArg(i);
            if (entityType != null && entityType.isAnnotationPresent(HbEntity.class)) {
                tableName = entityType.getAnnotation(HbEntity.class).table();
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
