package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.mapper.CrudMapper;
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
    public boolean match(Class<?> mapperClass, Method method) {
        return method.isAnnotationPresent(HbSelect.class)
            && method.getParameterCount() == 1
            && Reflections.isPojoClassQueryable(method.getParameterTypes()[0]);
    }

    @Override
    public MappedStatement createMappedStatement(
        Configuration mybatisConf, String sqlId, Class<?> mapperClass, Method method
    ) {
        var returnEntityType = getReturnEntityType(mapperClass, method);
        var selectMode = getSelectMode(method);

        SqlSourceForSelect sqlSource = new SqlSourceForSelect(
            sqlId, getCore(), mybatisConf, getTableName(mapperClass, method).getOrThrow(), selectMode, method);

        return MappedStatementHelper.buildMappedStatement(
            mybatisConf, sqlId, mapperClass, returnEntityType, sqlSource, SqlCommandType.SELECT
        );
    }

    private static Class<?> getReturnEntityType(Class<?> mapperClass, Method method) {
        if (CrudMapper.class.isAssignableFrom(mapperClass)) {
            return Reflections.getGenericTypeArg(mapperClass);
        } else {
            return Reflections.getReturnEntityType(method);
        }
    }

    /**
     * 判断一个 Mapper 方法是否只返回记录数
     */
    public static SelectMode getSelectMode(Method method) {
        Class<?> returnType = method.getReturnType();
        boolean counting =
            returnType == Short.TYPE ||
            returnType == Integer.TYPE ||
            returnType == Long.TYPE ||
            Number.class.isAssignableFrom(returnType);
        return counting ? SelectMode.Count : SelectMode.Normal;
    }
}
