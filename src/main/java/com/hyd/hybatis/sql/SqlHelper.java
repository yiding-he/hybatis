package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.reflection.Reflections;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SqlHelper {

    /**
     * 根据查询条件对象构建 Select
     *
     * @param queryObject 查询条件
     *
     * @return Select 对象
     */
    public static Sql.Select buildSelect(Object queryObject) {
        String tableName = getTableName(queryObject);
        if (tableName.length() > 7 && tableName.substring(0, 7).equalsIgnoreCase("select ")) {
            tableName = "(" + tableName + ") _tb_";
        }

        var select = Sql.Select("*").From(tableName);

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class);

        var conditionMappings = new HashMap<Field, Condition<?>>();
        var columnNameMappings = new HashMap<Condition<?>, String>();
        for (Field f : conditionFields) {
            Condition<?> condition = Reflections.getFieldValue(queryObject, f);
            columnNameMappings.put(condition, Reflections.getColumnName(f));
            conditionMappings.put(f, condition);
        }

        for (Field conditionField : conditionFields) {
            var condition = conditionMappings.get(conditionField);
            var columnName = columnNameMappings.get(condition);
            injectCondition(select, condition, columnName);
        }

        String orderBy = conditionMappings.values().stream()
            .filter(c -> c != null && (c.getOrderAsc() != null || c.getOrderDesc() != null))
            .sorted(Comparator.comparing(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc()))
            .map(c -> columnNameMappings.get(c) + (c.getOrderAsc() == null ? " desc" : " asc"))
            .collect(Collectors.joining(","));

        select.OrderBy(orderBy);
        return select;
    }

    public static void injectUpdateConditions(Sql.Update update, Object queryObject) {
        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class);

        for (Field f : conditionFields) {
            Condition<?> condition = Reflections.getFieldValue(queryObject, f);
            var columnName = Reflections.getColumnName(f);
            injectCondition(update, condition, columnName);
        }
    }

    private static String getTableName(Object queryObject) {
        var tableName = queryObject.getClass().getAnnotation(HbQuery.class).table().trim();
        if (tableName.isEmpty()) {
            throw new IllegalStateException(
                "Didn't see table() property of @HbQuery annotation on class '"
                    + queryObject.getClass().getCanonicalName() + "'");
        }
        return tableName;
    }

    private static void injectCondition(Sql<?> sql, Condition<?> condition, String columnName) {
        if (condition != null) {
            if (condition.getStartsWith() != null) {
                var value = condition.getStartsWith() + "%";
                sql.And(columnName + " like ?", value);
            }
            if (condition.getEndsWith() != null) {
                var value = "%" + condition.getEndsWith();
                sql.And(columnName + " like ?", value);
            }
            if (condition.getContains() != null) {
                var value = "%" + condition.getContains() + "%";
                sql.And(columnName + " like ?", value);
            }
            if (condition.getEq() != null) {
                var value = condition.getEq();
                sql.And(columnName + "=?", value);
            }
            if (condition.getNe() != null) {
                var value = condition.getNe();
                sql.And(columnName + "<>?", value);
            }
            if (condition.getNull() != null) {
                var value = condition.getNull();
                if (value) {
                    sql.And(columnName + " is null");
                } else {
                    sql.And(columnName + " is not null");
                }
            }
            if (condition.getLt() != null) {
                var value = condition.getLt();
                sql.And(columnName + "<?", value);
            }
            if (condition.getLte() != null) {
                var value = condition.getLte();
                sql.And(columnName + "<=?", value);
            }
            if (condition.getGt() != null) {
                var value = condition.getGt();
                sql.And(columnName + ">?", value);
            }
            if (condition.getGte() != null) {
                var value = condition.getGte();
                sql.And(columnName + ">=?", value);
            }
            if (condition.getIn() != null) {
                var value = condition.getIn();
                sql.And(columnName + " in ?", value);
            }
        }
    }
}
