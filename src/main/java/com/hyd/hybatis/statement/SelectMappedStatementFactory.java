package com.hyd.hybatis.statement;

import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

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
        SqlSourceForSelect sqlSource = new SqlSourceForSelect(configuration);
        return buildMappedStatement(configuration, sqlId, entityType, sqlSource);
    }
}
