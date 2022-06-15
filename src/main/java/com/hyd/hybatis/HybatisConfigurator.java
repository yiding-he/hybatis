package com.hyd.hybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * How to use: Add {@code @Import(HybatisConfigurator.class)} to your Spring Boot application class.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HybatisConfiguration.class)
public class HybatisConfigurator {

    @Bean
    Hybatis hybatis(HybatisConfiguration configuration) {
        return new Hybatis(configuration);
    }

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        var hybatis = event.getApplicationContext().getBean(Hybatis.class);
        var sqlSessionFactory = event.getApplicationContext().getBean(SqlSessionFactory.class);
        hybatis.process(sqlSessionFactory.getConfiguration());
    }
}
