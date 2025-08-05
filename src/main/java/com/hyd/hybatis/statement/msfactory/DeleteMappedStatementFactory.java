package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbDelete;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForDelete;
import com.hyd.hybatis.statement.MappedStatementHelper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class DeleteMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Class<?> mapperClass, Method method) {
        return method.isAnnotationPresent(HbDelete.class)
            && method.getParameterCount() == 1
            && Reflections.isPojoClassQueryable(method.getParameterTypes()[0]);
    }

    @Override
    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Class<?> mapperClass, Method method
    ) {
        SqlSourceForDelete sqlSource = new SqlSourceForDelete(
            sqlId, getCore(), configuration, getTableName(mapperClass, method).getOrThrow(),
            method
        );
        return MappedStatementHelper.buildMappedStatement(
                configuration, sqlId,mapperClass, sqlSource, SqlCommandType.DELETE);
    }
}
