package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.sql.SqlSourceForInsert;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class InsertMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        return getSqlCommandType(method) == SqlCommandType.INSERT
            && method.isAnnotationPresent(HbInsert.class)
            && method.getParameterCount() == 1;
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        var hbInsert = method.getAnnotation(HbInsert.class);
        var sqlSource = new SqlSourceForInsert(configuration, hbInsert.table());

        return buildMappedStatement(configuration, sqlId, sqlSource);

    }
}
