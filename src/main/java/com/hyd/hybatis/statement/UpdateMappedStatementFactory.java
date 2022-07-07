package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForUpdate;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class UpdateMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        return getSqlCommandType(method) == SqlCommandType.UPDATE
            && method.getParameterCount() == 2
            && Reflections.isPojoClassQueryable(method.getParameterTypes()[0]);
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        SqlSourceForUpdate sqlSource = new SqlSourceForUpdate(configuration, getTableName(method));
        return buildMappedStatement(configuration, sqlId, sqlSource, SqlCommandType.UPDATE);
    }
}
