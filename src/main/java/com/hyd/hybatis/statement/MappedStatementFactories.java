package com.hyd.hybatis.statement;

import com.hyd.hybatis.statement.msfactory.InsertMappedStatementFactory;
import com.hyd.hybatis.statement.msfactory.SelectMappedStatementFactory;
import com.hyd.hybatis.statement.msfactory.UpdateMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappedStatementFactories {

    public static final String METHOD_NAME_HINT = "Method name should start with: " +
        Stream.of(SqlCommandType.values()).map(t -> t.name().toLowerCase()).collect(Collectors.joining(", "));

    private final List<MappedStatementFactory> mappedStatementFactories = new ArrayList<>(Arrays.asList(
        new SelectMappedStatementFactory(),
        new InsertMappedStatementFactory(),
        new UpdateMappedStatementFactory()
    ));

    public void registerMappedStatementFactory(MappedStatementFactory mappedStatementFactory) {
        if (!mappedStatementFactories.contains(mappedStatementFactory)) {
            mappedStatementFactories.add(0, mappedStatementFactory);
        }
    }

    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Method method, boolean ignoreInvalidMethodName
    ) {
        var sqlCommandType = getSqlCommandType(method, ignoreInvalidMethodName);
        if (sqlCommandType == null) {
            return null;
        }

        var factory = mappedStatementFactories
            .stream().filter(f -> f.match(method)).findFirst().orElse(null);

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
