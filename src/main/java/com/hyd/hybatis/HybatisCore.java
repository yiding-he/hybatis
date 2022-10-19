package com.hyd.hybatis;

import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.statement.MappedStatementFactories;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

@Slf4j
public class HybatisCore {

    private final MappedStatementFactories mappedStatementFactories = new MappedStatementFactories(this);

    private final HybatisConfiguration conf;

    public HybatisCore(HybatisConfiguration conf) {
        this.conf = conf;
    }

    public HybatisConfiguration getConf() {
        return conf;
    }

    public MappedStatementFactories getMappedStatementFactories() {
        return mappedStatementFactories;
    }

    public void processMapperClass(Configuration configuration, Class<?> mapperClass) {
        var nonDefaultMethods = Reflections.getMapperNonDefaultMethods(mapperClass);
        nonDefaultMethods.forEach(method -> {
            try {
                processMapperMethod(configuration, mapperClass, method);
            } catch (Exception e) {
                log.error("Error processing method {}", method, e);
            }
        });
    }

    private void processMapperMethod(Configuration configuration, Class<?> mapperClass, Method abstractMethod) {
        var sqlId = mapperClass.getName() + "." + abstractMethod.getName();
        if (!configuration.hasStatement(sqlId)) {
            MappedStatement ms = mappedStatementFactories
                .createMappedStatement(configuration, sqlId, mapperClass, abstractMethod, true);
            if (ms != null) {
                configuration.addMappedStatement(ms);
                log.info("Created mapped statement for '{}'", sqlId);
            } else {
                log.info("Method '{}' not applicable for Hybatis.", sqlId);
            }
        }
    }

}
