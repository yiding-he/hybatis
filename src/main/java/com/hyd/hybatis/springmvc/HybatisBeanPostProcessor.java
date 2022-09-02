package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.HybatisCore;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class HybatisBeanPostProcessor implements BeanPostProcessor {

    private Configuration configuration;

    private HybatisCore hybatisCore;

    private final List<Class<?>> waitingMapperClasses = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            this.configuration = ((SqlSessionFactory) bean).getConfiguration();
            if (this.hybatisCore != null) {
                processWaitingList();
            }

        } else if (bean instanceof HybatisCore) {
            this.hybatisCore = (HybatisCore) bean;
            if (this.configuration != null) {
                processWaitingList();
            }

        } else if (bean instanceof MapperFactoryBean) {
            MapperFactoryBean<?> mapperFactoryBean = (MapperFactoryBean<?>) bean;
            Class<?> mapperClass = mapperFactoryBean.getMapperInterface();

            if (this.hybatisCore == null || this.configuration == null) {
                waitingMapperClasses.add(mapperClass);
            } else {
                this.hybatisCore.processMapperClass(configuration, mapperClass);
            }
        }
        return bean;
    }

    private void processWaitingList() {
        this.waitingMapperClasses.forEach(
            mapperClass -> this.hybatisCore.processMapperClass(this.configuration, mapperClass)
        );
    }
}
