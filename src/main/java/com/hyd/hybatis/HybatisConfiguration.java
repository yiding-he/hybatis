package com.hyd.hybatis;

import com.hyd.hybatis.page.Pagination;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "hybatis")
@Data
public class HybatisConfiguration {

    /**
     * Ignore bean properties which comes from specified types
     */
    private List<Class<?>> hideBeanFieldsFrom = Collections.emptyList();

    /**
     * Whether empty string should be ignored and not put into conditions
     */
    private boolean ignoreEmptyString = true;

    /**
     * Default page index parameter name
     */
    private String pageIndexParamName = Pagination.DEFAULT_PAGE_INDEX_NAME;

    /**
     * Default page size parameter name
     */
    private String pageSizeParamName = Pagination.DEFAULT_PAGE_SIZE_NAME;
}
