package com.hyd.hybatis.sql;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    public SqlSourceForSelect(HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName) {
        super(hybatisConfiguration, configuration, tableName);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        Sql.Select select;

        if (parameterObject instanceof Conditions) {
            select = SqlHelper.buildSelectFromConditions((Conditions) parameterObject, getTableName());
        } else {
            select = SqlHelper.buildSelect(parameterObject, getTableName());
        }

        return buildBoundSql(select);
    }

}
