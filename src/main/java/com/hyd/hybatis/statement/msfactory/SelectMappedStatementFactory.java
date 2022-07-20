package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class SelectMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        return method.isAnnotationPresent(HbSelect.class)
            && method.getParameterCount() == 1
            && Reflections.isPojoClassQueryable(method.getParameterTypes()[0]);
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        Class<?> returnEntityType = Reflections.getReturnEntityType(method);
        var fields = method.getAnnotation(HbSelect.class).fields();

        SqlSourceForSelect sqlSource = new SqlSourceForSelect(
            sqlId, getHybatisConfiguration(), configuration, getTableName(method));

        if (fields.length > 0) {
            sqlSource.setFields(fields);
        }

        return buildMappedStatement(configuration, sqlId, returnEntityType, sqlSource, SqlCommandType.SELECT);
    }
}
