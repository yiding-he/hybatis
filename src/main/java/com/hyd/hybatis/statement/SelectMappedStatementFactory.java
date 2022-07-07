package com.hyd.hybatis.statement;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class SelectMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        if (method.getParameterCount() != 1) {
            return false;
        }

        if (getSqlCommandType(method) != SqlCommandType.SELECT) {
            return false;
        }

        Class<?> paramType = method.getParameterTypes()[0];
        if (paramType == Conditions.class) {
            return true;
        }

        return Reflections.isPojoClassQueryable(paramType);
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        Class<?> returnEntityType = Reflections.getReturnEntityType(method);
        SqlSourceForSelect sqlSource = new SqlSourceForSelect(configuration, getTableName(method));
        return buildMappedStatement(configuration, sqlId, returnEntityType, sqlSource, SqlCommandType.SELECT);
    }
}
