package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.statement.MappedStatementFactories;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public abstract class HybatisSqlSource implements SqlSource {

    protected final String sqlId;

    protected final Configuration configuration;

    protected final HybatisCore core;

    protected final String tableName;

    protected String[] fields;

    protected HybatisSqlSource(
        String sqlId, HybatisCore core, Configuration configuration, String tableName
    ) {
        this.sqlId = sqlId;
        this.core = core;
        this.configuration = configuration;
        this.tableName = tableName;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String getSqlId() {
        return sqlId;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getTableName() {
        return tableName;
    }

    public HybatisConfiguration getHybatisConfiguration() {
        return this.core.getConf();
    }

    public MappedStatementFactories getMappedStatementFactories() {
        return this.core.getMappedStatementFactories();
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

    protected HybatisCore getCore() {
        return this.core;
    }
}
