package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForUpdate;
import com.hyd.hybatis.statement.MappedStatementHelper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class UpdateMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Class<?> mapperClass, Method method) {
        return method.isAnnotationPresent(HbUpdate.class) &&
               method.getParameterCount() == 2 &&
               Reflections.isPojoClassQueryable(method.getParameterTypes()[0]);
    }

    @Override
    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Class<?> mapperClass, Method method
    ) {
        SqlSourceForUpdate sqlSource = new SqlSourceForUpdate(
            sqlId, getCore(), configuration, getTableName(mapperClass, method).getOrThrow(), method
        );
        return MappedStatementHelper.buildMappedStatement(configuration, sqlId, sqlSource, SqlCommandType.UPDATE);
    }
}
