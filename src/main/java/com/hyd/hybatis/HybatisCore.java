package com.hyd.hybatis;

import com.hyd.hybatis.interceptor.HybatisPageInterceptor;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.statement.MappedStatementFactories;
import com.hyd.hybatis.statement.MappedStatementFactory;
import com.hyd.hybatis.statement.msfactory.AbstractMappedStatementFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.stream.Stream;

@Slf4j
public class HybatisCore {

    private final MappedStatementFactories mappedStatementFactories = new MappedStatementFactories();

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

    public void process(Configuration configuration) {
        log.info("Processing mybatis configuration {}...", configuration);

        // Inject HybatisConfiguration into MappedStatementFactory instances
        mappedStatementFactories.getMappedStatementFactories().forEach(f -> {
            if (f instanceof AbstractMappedStatementFactory) {
                ((AbstractMappedStatementFactory) f).setHybatisConfiguration(this.conf);
            }
        });

        // Create MappedStatement for appropriate Mapper methods
        configuration.getMapperRegistry().getMappers().forEach(mapperClass -> {
            processMapperClass(configuration, mapperClass);
        });

        // Add interceptor for pagination queries
        configuration.addInterceptor(new HybatisPageInterceptor(this));
    }

    private void processMapperClass(Configuration configuration, Class<?> mapperClass) {
        Stream.of(mapperClass.getDeclaredMethods()).forEach(method -> {
            try {
                processMapperMethod(configuration, mapperClass, method);
            } catch (Exception e) {
                log.error("Error processing method {}", method, e);
            }
        });
    }

    private void processMapperMethod(Configuration configuration, Class<?> mapperClass, Method method) {
        var sqlId = mapperClass.getName() + "." + method.getName();
        if (!configuration.hasStatement(sqlId) && !Reflections.hasBody(method)) {
            MappedStatement ms = mappedStatementFactories.createMappedStatement(configuration, sqlId, method, true);
            if (ms != null) {
                configuration.addMappedStatement(ms);
                log.info("Created mapped statement for '{}'", sqlId);
            } else {
                log.info("Method '{}' not applicable for Hybatis.", sqlId);
            }
        }
    }

}
