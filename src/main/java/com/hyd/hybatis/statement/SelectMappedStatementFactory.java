package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.sql.SelectSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.Collections;

public class SelectMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        var sqlCommandType = getSqlCommandType(method);
        Class<?> queryParamType = getHbQueryParamType(method);
        return sqlCommandType == SqlCommandType.SELECT && queryParamType != null;
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        // todo implement SelectMappedStatementFactory.createMappedStatement()

        var hbQueryType = getHbQueryParamType(method);
        var hbQuery = hbQueryType.getAnnotation(HbQuery.class);
        Class<?> entityType = hbQuery.entity();
        var sqlSource = new SelectSqlSource(configuration);

        return new MappedStatement.Builder(
            configuration, sqlId, sqlSource, SqlCommandType.SELECT
        ).resultMaps(Collections.singletonList(
            new ResultMap.Builder(configuration, sqlId + "_RM", entityType, Collections.emptyList(), true).build()
        )).build();
    }
}
