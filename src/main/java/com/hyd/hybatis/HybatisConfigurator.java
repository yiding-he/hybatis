package com.hyd.hybatis;

import com.hyd.hybatis.springmvc.HybatisBeanPostProcessor;
import com.hyd.hybatis.springmvc.HybatisMvcConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * How to use: Add {@code @Import(HybatisConfigurator.class)} to your Spring Boot Configuration class.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HybatisConfiguration.class)
@Import({
    HybatisBeanPostProcessor.class,
    HybatisMvcConfigurer.class
})
@ConditionalOnBean(SqlSessionFactory.class)
public class HybatisConfigurator {

    @Bean
    HybatisCore hybatisCore(HybatisConfiguration configuration) {
        return new HybatisCore(configuration);
    }

    @Bean
    Hybatis hybatis(
        HybatisConfiguration configuration,
        SqlSessionFactory sqlSessionFactory
    ) {
        return new Hybatis(configuration, sqlSessionFactory);
    }
}
