package com.hyd.hybatis.sql;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    public SqlSourceForSelect(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        Sql.Select select = SqlHelper.buildSelect(parameterObject);
        return buildBoundSql(select);
    }

}
