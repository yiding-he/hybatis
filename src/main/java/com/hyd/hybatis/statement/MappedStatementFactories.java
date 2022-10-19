package com.hyd.hybatis.statement;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.statement.msfactory.*;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.*;

public class MappedStatementFactories {

    private final List<? extends MappedStatementFactory> mappedStatementFactories =
        new ArrayList<>(List.of(
            new SelectMappedStatementFactory(),
            new InsertMappedStatementFactory(),
            new UpdateMappedStatementFactory(),
            new DeleteMappedStatementFactory()
        ));

    public MappedStatementFactories(HybatisCore core) {
        this.init(core);
    }

    private void init(HybatisCore core) {
        this.mappedStatementFactories.forEach(f -> {
            if (f instanceof AbstractMappedStatementFactory) {
                ((AbstractMappedStatementFactory) f).setCore(core);
            }
        });
    }

    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Class<?> mapperClass, Method method, boolean ignoreInvalidMethodName
    ) {
        MappedStatementFactory factory = getMappedStatementFactory(mapperClass, method);
        return factory == null ? null : factory.createMappedStatement(configuration, sqlId, mapperClass, method);
    }

    public MappedStatementFactory getMappedStatementFactory(Class<?> mapperClass, Method method) {
        return mappedStatementFactories
            .stream().filter(f -> f.match(mapperClass, method)).findFirst().orElse(null);
    }
}
