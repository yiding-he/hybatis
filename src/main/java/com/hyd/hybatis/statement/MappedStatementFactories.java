package com.hyd.hybatis.statement;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.statement.msfactory.AbstractMappedStatementFactory;
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
        Configuration configuration, String sqlId, Method method, boolean ignoreInvalidMethodName
    ) {
        MappedStatementFactory factory = getMappedStatementFactory(method);
        return factory == null ? null : factory.createMappedStatement(configuration, sqlId, method);

    }

    /**
     * Check whether a given method can be processed by Hybatis
     */
    public boolean isValidHybatisMethod(Method method) {
        return getMappedStatementFactory(method) != null;
    }

    public MappedStatementFactory getMappedStatementFactory(Method method) {
        return mappedStatementFactories
            .stream().filter(f -> f.match(method)).findFirst().orElse(null);
    }
}
