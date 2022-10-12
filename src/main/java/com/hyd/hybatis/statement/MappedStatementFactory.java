package com.hyd.hybatis.statement;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MappedStatementFactory {

    default SqlCommandType getSqlCommandType(Method method) {
        return Stream.of(SqlCommandType.values())
            .filter(t -> method.getName().startsWith(t.name().toLowerCase()))
            .findFirst()
            .orElse(null);
    }

    boolean match(Class<?> mapperClass, Method method);

    MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Class<?> mapperClass, Method method);
}
