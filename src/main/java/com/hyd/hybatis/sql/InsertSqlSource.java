package com.hyd.hybatis.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

public class InsertSqlSource  extends HybatisSqlSource {

    public InsertSqlSource(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        return null;// TODO implement com.hyd.hybatis.sql.InsertSqlSource.build()
    }
}
