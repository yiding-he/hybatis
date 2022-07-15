package com.hyd.hybatis;

import com.hyd.hybatis.reflection.Reflections;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.ibatis.reflection.TypeParameterResolver.resolveReturnType;

public class JavaGenericsTest {

    private final List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public static class MyQuery {

        public Condition<Date> startDate;
    }

    @Test
    public void test() throws Exception {
        Type returnType;

        returnType = resolveReturnType(getClass().getMethod("getList"), JavaGenericsTest.class);
        System.out.println("getList() returns " + returnType);

        returnType = resolveReturnType(getClass().getMethod("hashCode"), JavaGenericsTest.class);
        System.out.println("hashCode() returns " + returnType);
    }

    @Test
    public void testFieldGenericType() throws Exception {
        var field = MyQuery.class.getField("startDate");
        System.out.println("field.getGenericType() = " + field.getGenericType());
        Class<?> genericTypeArg = Reflections.getGenericTypeArg(field.getGenericType());
        System.out.println("genericTypeArg = " + genericTypeArg);
    }
}
