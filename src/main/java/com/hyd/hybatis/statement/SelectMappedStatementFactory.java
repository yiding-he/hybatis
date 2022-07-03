package com.hyd.hybatis.statement;

import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.reflection.Reflections;
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
        Class<?> entityType = Reflections.getReturnEntityType(method);
        var sqlSource = new SelectSqlSource(configuration);

        ResultMap resultMap = new ResultMap
            .Builder(configuration, sqlId + "_RM", entityType, Collections.emptyList(), true)
            .build();

        return new MappedStatement
            .Builder(configuration, sqlId, sqlSource, SqlCommandType.SELECT)
            .lang(new HybatisLanguageDriver())
            .resultMaps(Collections.singletonList(resultMap))
            .build();
    }
}
