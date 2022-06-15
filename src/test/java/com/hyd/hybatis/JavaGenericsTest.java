package com.hyd.hybatis;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.apache.ibatis.reflection.TypeParameterResolver.resolveReturnType;

public class JavaGenericsTest {

    private final List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    @Test
    public void test() throws Exception {
        Type returnType;

        returnType = resolveReturnType(getClass().getMethod("getList"), JavaGenericsTest.class);
        System.out.println("getList() returns " + returnType);

        returnType = resolveReturnType(getClass().getMethod("hashCode"), JavaGenericsTest.class);
        System.out.println("hashCode() returns " + returnType);
    }
}
