package com.hyd.hybatis.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public abstract class HybatisSqlSource implements SqlSource {

    private final Configuration configuration;

    private final String tableName;

    protected HybatisSqlSource(Configuration configuration, String tableName) {
        this.configuration = configuration;
        this.tableName = tableName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getTableName() {
        return tableName;
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
