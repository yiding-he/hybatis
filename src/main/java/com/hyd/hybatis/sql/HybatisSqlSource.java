package com.hyd.hybatis.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public abstract class HybatisSqlSource implements SqlSource {

    private final Configuration configuration;

    protected HybatisSqlSource(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return build(parameterObject);
    }

    protected abstract BoundSql build(Object parameterObject);
}
