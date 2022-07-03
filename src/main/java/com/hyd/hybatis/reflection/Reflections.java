package com.hyd.hybatis.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflections {

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
}
