package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.HbQuery;
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
            && method.getParameterTypes()[0].isAnnotationPresent(HbQuery.class);
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        String tableName = method.getParameterTypes()[0].getAnnotation(HbQuery.class).table();
        SqlSourceForUpdate sqlSource = new SqlSourceForUpdate(configuration, tableName);
        return buildMappedStatement(configuration, sqlId, sqlSource, SqlCommandType.UPDATE);
    }
}
