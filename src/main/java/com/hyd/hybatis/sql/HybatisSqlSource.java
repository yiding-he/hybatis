package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public abstract class HybatisSqlSource implements SqlSource {

    private final Configuration configuration;

    private final HybatisConfiguration hybatisConfiguration;

    private final String tableName;

    protected HybatisSqlSource(
        HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName
    ) {
        this.hybatisConfiguration = hybatisConfiguration;
        this.configuration = configuration;
        this.tableName = tableName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getTableName() {
        return tableName;
    }

    public HybatisConfiguration getHybatisConfiguration() {
        return hybatisConfiguration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return build(parameterObject);
    }

    protected abstract BoundSql build(Object parameterObject);

    // Helper method for subclasses
    protected BoundSql buildBoundSql(Sql<?> sql) {
        return new BoundSqlBuilder(configuration, sql).build();
    }
}