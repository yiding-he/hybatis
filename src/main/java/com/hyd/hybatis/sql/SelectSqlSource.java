package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.reflection.Reflections;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectSqlSource extends HybatisSqlSource {

    public SelectSqlSource(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        var tableName = parameterObject.getClass().getAnnotation(HbQuery.class).table();
        var select = Sql.Select("*").From(tableName);
        Map<String, Object> parameterMap = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();

        var conditionFields = Reflections.getPojoFieldsOfType(parameterObject.getClass(), Condition.class);
        for (Field conditionField : conditionFields) {
            var condition = getCondition(parameterObject, conditionField);
            var fieldName = conditionField.getName();
            var columnName = getColumnName(fieldName);
            if (condition != null) {
                if (condition.getEq() != null) {
                    Object value = condition.getEq();
                    select.And(columnName + "=?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
            }
        }

        return new BoundSql(
            getConfiguration(),
            select.getSql(),
            parameterMappings,
            parameterMap
        );
    }

    private ParameterMapping getParameterMapping(String fieldName, Object value) {
        return new ParameterMapping.Builder(getConfiguration(), fieldName, value.getClass()).build();
    }

    private String getColumnName(String fieldName) {
        return fieldName.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    private Condition<?> getCondition(Object parameterObject, Field conditionField) {
        try {
            var fieldName = conditionField.getName();
            var getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            var getterMethod = conditionField.getDeclaringClass().getMethod(getterName);
            if (Modifier.isPublic(getterMethod.getModifiers())) {
                var fieldValue = getterMethod.invoke(parameterObject);
                return (Condition<?>) fieldValue;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
