package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.HybatisConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
public class HybatisMvcConfigurer implements WebMvcConfigurer {

    private final HybatisConfiguration hybatisConfiguration;

    public HybatisMvcConfigurer(HybatisConfiguration hybatisConfiguration) {
        this.hybatisConfiguration = hybatisConfiguration;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(0, new HybatisHandlerMethodArgumentResolver(hybatisConfiguration));
        log.info("HybatisHandlerMethodArgumentResolver added to resolvers");
    }
}
