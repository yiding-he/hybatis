package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbColumn;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.reflection.Reflections;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SelectSqlSource extends HybatisSqlSource {

    public SelectSqlSource(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        var tableName = parameterObject.getClass().getAnnotation(HbQuery.class).table().trim();
        if (tableName.length() > 7 && tableName.substring(0, 7).equalsIgnoreCase("select ")) {
            tableName = "(" + tableName + ") _tb_";
        }

        var select = Sql.Select("*").From(tableName);
        Map<String, Object> parameterMap = new HashMap<>();
        Map<String, Object> additionalParameterMap = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();

        var conditionFields = Reflections
            .getPojoFieldsOfType(parameterObject.getClass(), Condition.class);

        var conditionMappings = new HashMap<Field, Condition<?>>();
        var columnNameMappings = new HashMap<Condition<?>, String>();
        for (Field f : conditionFields) {
            Condition<?> condition = getCondition(parameterObject, f);
            columnNameMappings.put(condition, getColumnName(f));
            conditionMappings.put(f, condition);
        }

        for (Field conditionField : conditionFields) {
            var condition = conditionMappings.get(conditionField);
            var fieldName = conditionField.getName();
            var columnName = columnNameMappings.get(condition);

            if (condition != null) {
                if (condition.getStartsWith() != null) {
                    var paramName = fieldName + ":startsWith";
                    var value = condition.getStartsWith() + "%";
                    select.And(columnName + " like ?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getEndsWith() != null) {
                    var paramName = fieldName + ":endsWith";
                    var value = "%" + condition.getEndsWith();
                    select.And(columnName + " like ?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getContains() != null) {
                    var paramName = fieldName + ":contains";
                    var value = "%" + condition.getContains() + "%";
                    select.And(columnName + " like ?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getEq() != null) {
                    var paramName = fieldName + ":eq";
                    var value = condition.getEq();
                    select.And(columnName + "=?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getNe() != null) {
                    var paramName = fieldName + ":ne";
                    var value = condition.getNe();
                    select.And(columnName + "<>?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
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
                    var paramName = fieldName + ":lt";
                    var value = condition.getLt();
                    select.And(columnName + "<?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getLte() != null) {
                    var paramName = fieldName + ":lte";
                    var value = condition.getLte();
                    select.And(columnName + "<=?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getGt() != null) {
                    var paramName = fieldName + ":gt";
                    var value = condition.getGt();
                    select.And(columnName + ">?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getGte() != null) {
                    var paramName = fieldName + ":gte";
                    var value = condition.getGte();
                    select.And(columnName + ">=?", value);
                    parameterMap.put(paramName, value);
                    parameterMappings.add(getParameterMapping(paramName, value));
                    additionalParameterMap.put(paramName, value);
                }
                if (condition.getIn() != null) {
                    var value = condition.getIn();
                    select.And(columnName + " in ?", value);

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

        String orderBy = conditionMappings.values().stream()
            .filter(c -> c != null && (c.getOrderAsc() != null || c.getOrderDesc() != null))
            .sorted(Comparator.comparing(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc()))
            .map(c -> columnNameMappings.get(c) + (c.getOrderAsc() == null ? " desc" : " asc"))
            .collect(Collectors.joining(","));
        select.OrderBy(orderBy);

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

    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(HbColumn.class)) {
            return field.getAnnotation(HbColumn.class).value();
        } else {
            return field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
        }
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
