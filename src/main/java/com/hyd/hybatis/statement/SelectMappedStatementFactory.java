package com.hyd.hybatis.statement;

import com.hyd.hybatis.annotations.Table;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;

public class SelectMappedStatementFactory implements MappedStatementFactory {

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        // todo implement SelectMappedStatementFactory.createMappedStatement()

        Class<?> entityType = findEntityType(method);
        var tableName = entityType.getAnnotation(Table.class).value();
        var sql = "select * from " + tableName;
        var sqlSource = new SqlSourceBuilder(configuration).parse(sql, entityType, null);

        return new MappedStatement.Builder(
            configuration, sqlId, sqlSource, SqlCommandType.SELECT
        ).resultMaps(Collections.singletonList(
            new ResultMap.Builder(configuration, sqlId + "_RM", entityType, Collections.emptyList(), true).build()
        )).build();
    }

    private Class<?> findEntityType(Method method) {
        var type = TypeParameterResolver.resolveReturnType(method, method.getDeclaringClass());
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        } else {
            return (Class<?>) type;
        }
    }
}
