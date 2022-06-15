package com.hyd.hybatis;

import com.hyd.hybatis.statement.MappedStatementFactories;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.stream.Stream;

@Slf4j
public class Hybatis {

    private final MappedStatementFactories mappedStatementFactories = new MappedStatementFactories();

    private final HybatisConfiguration configuration;

    public Hybatis(HybatisConfiguration configuration) {
        this.configuration = configuration;
    }

    public void process(Configuration configuration) {
        log.info("Processing mybatis configuration {}", configuration);

        configuration.getMapperRegistry().getMappers().forEach(mapperClass -> {
            processMapperClass(configuration, mapperClass);
        });
    }

    private void processMapperClass(Configuration configuration, Class<?> mapperClass) {
        Stream.of(mapperClass.getDeclaredMethods()).forEach(method -> {
            processMapperMethod(configuration, mapperClass, method);
        });
    }

    private void processMapperMethod(Configuration configuration, Class<?> mapperClass, Method method) {
        var sqlId = mapperClass.getName() + "." + method.getName();
        if (!configuration.hasStatement(sqlId)) {
            log.info("MappedStatement '{}' not found.", sqlId);
            MappedStatement ms = mappedStatementFactories
                .createMappedStatement(configuration, sqlId, method, true);
            if (ms != null) {
                configuration.addMappedStatement(ms);
            }
        }
    }
}
