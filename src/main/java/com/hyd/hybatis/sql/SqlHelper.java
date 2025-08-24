package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;

public class SqlHelper {

    @Data
    public static class Context {

        private final Object paramObject;

        private final String tableName;

        private final HybatisConfiguration config;
    }

    ////////////////////////////////////////

    public static Sql.Delete buildDelete(Context context) {
        var delete = Sql.Delete(context.tableName);
        injectConditionObject(context, delete);
        return delete;
    }

    public static Sql.Delete buildDeleteFromConditions(Context context) {
        Conditions conditions = (Conditions) context.paramObject;
        return buildDeleteFromConditions(conditions, context.tableName);
    }

    public static Sql.Delete buildDeleteFromConditions(Conditions conditions, String tableName) {
        Sql.Delete delete = new Sql.Delete(tableName);
        for (Condition condition : conditions.conditionsList()) {
            injectCondition(delete, condition);
        }
        return delete;
    }

    ////////////////////////////////////////

    public static Sql.Select buildSelect(Context context) {
        var select = Sql.Select("*").From(context.tableName);
        injectConditionObject(context, select);
        return select;
    }

    public static Sql.Select buildSelectFromConditions(Context context) {
        var conditions = (Conditions) context.paramObject;
        var tableName = context.tableName;
        return buildSelectFromConditions(conditions, tableName);
    }

    public static Sql.Select buildSelectFromConditions(Conditions conditions, String tableName) {
        var projection = conditions.getProjection();
        var columns = projection.isEmpty() ? "*" : String.join(",", projection);
        Sql.Select select = new Sql.Select(columns).From(tableName);
        for (Condition condition : conditions.conditionsList()) {
            injectCondition(select, condition);
        }

        if (conditions.getLimit() >= 0) {
            select.Limit(conditions.getLimit());
        }
        return select;
    }

    /// /////////////////////////////////////

    private static void injectConditionObject(Context context, Sql<?> sql) {
        var queryObject = context.paramObject;

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        var conditionMappings = new HashMap<Field, Condition>();
        var camelToUnderline = context.getConfig().isCamelToUnderline();
        for (Field f : conditionFields) {
            Condition condition = Reflections.getFieldValue(queryObject, f);
            if (condition == null) {
                continue;
            }

            var columnName = Reflections.getColumnName(f, camelToUnderline);
            condition.setColumn(columnName);
            conditionMappings.put(f, condition);
        }

        for (Field conditionField : conditionFields) {
            var condition = conditionMappings.get(conditionField);
            injectCondition(sql, condition);
        }
    }

    /**
     * 分析 queryObject，将查询条件放入 update 中，返回条件字段列表
     */
    public static void injectUpdateConditions(Sql.Update update, Object queryObject) {

        if (queryObject instanceof Conditions) {
            ((Conditions) queryObject).conditionsList().forEach(
                c -> injectCondition(update, c)
            );
        }

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        for (Field f : conditionFields) {
            Condition c = Reflections.getFieldValue(queryObject, f);
            if (c == null) {
                continue;
            }
            injectCondition(update, c);
        }

    }

    ///////////////////////////////////////////////////////////////////

    public static void injectCondition(Sql<?> sql, Condition condition) {
        if (sql != null && condition != null) {
            condition.getOperator().operate(sql, condition.getColumn(), condition.getValues());
        }
    }
}
