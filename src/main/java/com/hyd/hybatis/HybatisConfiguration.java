package com.hyd.hybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "hybatis")
@Data
public class HybatisConfiguration {

    private List<Class<?>> hideBeanFieldsFrom = Collections.emptyList();
}
