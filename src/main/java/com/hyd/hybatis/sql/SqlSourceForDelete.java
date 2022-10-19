package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

@Slf4j
public class SqlSourceForDelete extends HybatisSqlSource {

    public SqlSourceForDelete(
        String sqlId, HybatisCore core, Configuration configuration, String tableName,
        Method mapperMethod
    ) {
        super(sqlId, core, configuration, tableName, mapperMethod);
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var context = new SqlHelper.Context(
            parameterObject, getTableName(), getHybatisConfiguration());

        Sql.Delete delete;
        if (parameterObject instanceof Conditions) {
            delete = SqlHelper.buildDeleteFromConditions(context);
        } else {
            delete = SqlHelper.buildDelete(context);
        }

        log.debug("[{}]: {}", getSqlId(), delete.toCommand());
        return buildBoundSql(delete);
    }

}
