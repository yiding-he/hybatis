package com.hyd.hybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(HybatisConfiguration.class)
public class HybatisConfigurator {

    @Bean
    FactoryBean<Hybatis> hybatis(HybatisConfiguration configuration) {
        log.info("Hybatis activated.");
        return new FactoryBean<>() {
            @Override
            public Hybatis getObject() throws Exception {
                return new Hybatis(configuration);
            }

            @Override
            public Class<?> getObjectType() {
                return Hybatis.class;
            }
        };
    }
}
