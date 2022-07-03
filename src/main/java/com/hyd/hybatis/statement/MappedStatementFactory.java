package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.HbQuery;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public interface MappedStatementFactory {

    default SqlCommandType getSqlCommandType(Method method) {
        return Stream.of(SqlCommandType.values())
            .filter(t -> method.getName().startsWith(t.name().toLowerCase()))
            .findFirst()
            .orElse(null);
    }

    default Class<?> getHbQueryParamType(Method method) {
        var parameterTypes = method.getParameterTypes();
        return Stream.of(parameterTypes)
            .filter(t -> t.isAnnotationPresent(HbQuery.class))
            .findFirst().orElse(null);
    }

    boolean match(Method method);

    MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method);
}
