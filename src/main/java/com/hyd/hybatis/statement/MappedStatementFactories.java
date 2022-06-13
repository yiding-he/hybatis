package com.hyd.hybatis.statement;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappedStatementFactories {

    public static final String METHOD_NAME_HINT = "Method name should start with: " +
        Stream.of(SqlCommandType.values()).map(t -> t.name().toLowerCase()).collect(Collectors.joining(", "));

    private final Map<SqlCommandType, MappedStatementFactory> factoryMap = new HashMap<>();

    {
        factoryMap.put(SqlCommandType.SELECT, new SelectMappedStatementFactory());
        factoryMap.put(SqlCommandType.INSERT, new InsertMappedStatementFactory());
        factoryMap.put(SqlCommandType.UPDATE, new UpdateMappedStatementFactory());
        factoryMap.put(SqlCommandType.DELETE, new DeleteMappedStatementFactory());
    }

    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Method method, boolean ignoreInvalidMethodName
    ) {
        var sqlCommandType = getSqlCommandType(method, ignoreInvalidMethodName);
        if (sqlCommandType == null) {
            return null;
        }

        var factory = factoryMap.get(sqlCommandType);
        if (factory == null) {
            return null;
        }

        return factory.createMappedStatement(configuration, sqlId, method);
    }

    private SqlCommandType getSqlCommandType(Method method, boolean ignoreInvalidMethodName) {

        var type = Stream.of(SqlCommandType.values())
            .filter(t -> method.getName().startsWith(t.name().toLowerCase()))
            .findFirst();

        if (ignoreInvalidMethodName) {
            return type.orElse(null);
        } else {
            return type.orElseThrow(() -> new IllegalArgumentException(
                "Unsupported method name: " + method.getName() + ", " + METHOD_NAME_HINT)
            );
        }
    }
}
