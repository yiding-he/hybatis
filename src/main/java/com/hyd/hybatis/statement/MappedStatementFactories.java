package com.hyd.hybatis.statement;

import com.hyd.hybatis.statement.msfactory.InsertMappedStatementFactory;
import com.hyd.hybatis.statement.msfactory.SelectMappedStatementFactory;
import com.hyd.hybatis.statement.msfactory.UpdateMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.*;

public class MappedStatementFactories {

    private final List<MappedStatementFactory> mappedStatementFactories = new ArrayList<>(Arrays.asList(
        new SelectMappedStatementFactory(),
        new InsertMappedStatementFactory(),
        new UpdateMappedStatementFactory()
    ));

    public List<MappedStatementFactory> getMappedStatementFactories() {
        return Collections.unmodifiableList(mappedStatementFactories);
    }

    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Method method, boolean ignoreInvalidMethodName
    ) {
        var factory = mappedStatementFactories
            .stream().filter(f -> f.match(method)).findFirst().orElse(null);

        if (factory == null) {
            return null;
        }

        return factory.createMappedStatement(configuration, sqlId, method);
    }

}
