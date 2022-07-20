package com.hyd.hybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "hybatis")
@Data
public class HybatisConfiguration {

    /**
     * 在解析 Java Bean 对象属性时，忽略来自哪些父类的属性
     */
    private List<Class<?>> hideBeanFieldsFrom = Collections.emptyList();

    private boolean ignoreEmptyString = true;
}
