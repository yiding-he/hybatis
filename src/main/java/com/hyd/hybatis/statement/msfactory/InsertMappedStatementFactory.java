package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.sql.SqlSourceForInsert;
import com.hyd.hybatis.statement.MappedStatementHelper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class InsertMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Class<?> mapperClass, Method method) {
        return method.isAnnotationPresent(HbInsert.class)
            && method.getParameterCount() == 1;
    }

    @Override
    public MappedStatement createMappedStatement(
        Configuration configuration, String sqlId, Class<?> mapperClass, Method method
    ) {
        var sqlSource = new SqlSourceForInsert(
            sqlId, getCore(), configuration, getTableName(mapperClass, method).getOrThrow(), method
        );
        return MappedStatementHelper.buildMappedStatement(
                configuration, sqlId, mapperClass, sqlSource, SqlCommandType.INSERT);
    }
}
