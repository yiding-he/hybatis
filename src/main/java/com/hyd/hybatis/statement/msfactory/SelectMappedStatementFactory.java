package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.sql.SelectMode;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import com.hyd.hybatis.statement.MappedStatementHelper;
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
        var selectMode = counting ? SelectMode.Count : SelectMode.Normal;

        SqlSourceForSelect sqlSource = new SqlSourceForSelect(
            sqlId, getCore(), mybatisConf, getTableName(method), selectMode, method);

        if (fields.length > 0) {
            sqlSource.setFields(fields);
        }

        return MappedStatementHelper.buildMappedStatement(
            mybatisConf, sqlId, returnEntityType, sqlSource, SqlCommandType.SELECT
        );
    }

    /**
     * 判断一个 Mapper 方法是否只返回记录数
     */
    public static boolean isCounting(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType == Integer.TYPE ||
            returnType == Long.TYPE ||
            Number.class.isAssignableFrom(returnType);
    }
}
