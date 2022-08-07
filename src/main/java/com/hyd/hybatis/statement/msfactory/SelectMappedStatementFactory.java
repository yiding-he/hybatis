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
    public MappedStatement createMappedStatement(Configuration mybatisConf, String sqlId, Method method) {
        Class<?> returnEntityType = Reflections.getReturnEntityType(method);
        var fields = method.getAnnotation(HbSelect.class).fields();
        var counting = isCounting(method);
        var hybatisConf = getHybatisConfiguration();

        SqlSourceForSelect sqlSource = new SqlSourceForSelect(sqlId, hybatisConf, mybatisConf, getTableName(method));
        sqlSource.setCounting(counting);

        if (fields.length > 0) {
            sqlSource.setFields(fields);
        }

        return buildMappedStatement(mybatisConf, sqlId, returnEntityType, sqlSource, SqlCommandType.SELECT);
    }

    public static boolean isCounting(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType == Integer.TYPE ||
            returnType == Long.TYPE ||
            Number.class.isAssignableFrom(returnType);
    }
}
