package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.reflection.Reflections;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class SelectSqlSource extends HybatisSqlSource {

    public SelectSqlSource(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        var tableName = parameterObject.getClass().getAnnotation(HbQuery.class).table();
        var select = Sql.Select("*").From(tableName);
        Map<String, Object> parameterMap = new HashMap<>();
        Map<String, Object> additionalParameterMap = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();

        var conditionFields = Reflections.getPojoFieldsOfType(parameterObject.getClass(), Condition.class);
        for (Field conditionField : conditionFields) {
            var condition = getCondition(parameterObject, conditionField);
            var fieldName = conditionField.getName();
            var columnName = getColumnName(fieldName);
            if (condition != null) {
                if (condition.getStartsWith() != null) {
                    var value = condition.getStartsWith() + "%";
                    select.And(columnName + " like ?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getEndsWith() != null) {
                    var value = "%" + condition.getEndsWith();
                    select.And(columnName + " like ?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getContains() != null) {
                    var value = "%" + condition.getContains() + "%";
                    select.And(columnName + " like ?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getEq() != null) {
                    var value = condition.getEq();
                    select.And(columnName + "=?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getNe() != null) {
                    var value = condition.getNe();
                    select.And(columnName + "<>?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getNull() != null) {
                    var value = condition.getNull();
                    if (value) {
                        select.And(columnName + " is null");
                    } else {
                        select.And(columnName + " is not null");
                    }
                }
                if (condition.getLt() != null) {
                    var value = condition.getLt();
                    select.And(columnName + "<?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getLte() != null) {
                    var value = condition.getLte();
                    select.And(columnName + "<=?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getGt() != null) {
                    var value = condition.getGt();
                    select.And(columnName + ">?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getGte() != null) {
                    var value = condition.getGte();
                    select.And(columnName + ">=?", value);
                    parameterMap.put(fieldName, value);
                    parameterMappings.add(getParameterMapping(fieldName, value));
                }
                if (condition.getIn() != null) {
                    var value = condition.getIn();
                    var marks = IntStream.range(0, value.size())
                        .mapToObj(i -> "?").collect(Collectors.joining(","));

                    select.And(columnName + " in (" + marks + ")");

                    for (int i = 0; i < value.size(); i++) {
                        Object o = value.get(i);
                        String paramName = fieldName + ":" + i;
                        parameterMappings.add(getParameterMapping(paramName, o));
                        parameterMap.put(paramName, o);
                        additionalParameterMap.put(paramName, o);
                    }
                }
            }
        }

        SqlCommand sqlCommand = select.toCommand();
        log.info(sqlCommand.toString());

        ///////////////////////////////////////////
        // Constructing a BoundSql object:
        // After trying a hundred times I finally made it work.
        // I don't know exactly why.

        BoundSql boundSql = new BoundSql(
            getConfiguration(),
            sqlCommand.getStatement(),
            parameterMappings,
            parameterMap
        );
        additionalParameterMap.forEach(
            boundSql::setAdditionalParameter
        );

        ///////////////////////////////////////////

        return boundSql;
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
