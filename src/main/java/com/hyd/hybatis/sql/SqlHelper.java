package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        for (Condition<?> condition : conditions.getConditions()) {
            injectCondition(delete, condition);
        }
        return delete;
    }

    ////////////////////////////////////////

    public static Sql.Select buildSelect(Context context) {
        var select = Sql.Select("*").From(context.tableName);
        HashMap<Field, Condition<?>> conditionMappings = injectConditionObject(context, select);
        injectOrderBy(select, conditionMappings.values());
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
        for (Condition<?> condition : conditions.getConditions()) {
            injectCondition(select, condition);
        }
        injectOrderBy(select, conditions.getConditions());

        if (conditions.getLimit() >= 0) {
            select.Limit(conditions.getLimit());
        }
        return select;
    }

    ////////////////////////////////////////

    private static HashMap<Field, Condition<?>> injectConditionObject(Context context, Sql<?> sql) {
        var queryObject = context.paramObject;

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        var conditionMappings = new HashMap<Field, Condition<?>>();
        var camelToUnderline = context.getConfig().isCamelToUnderline();
        for (Field f : conditionFields) {
            Condition<?> condition = Reflections.getFieldValue(queryObject, f);
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
        return conditionMappings;
    }

    /**
     * 分析 queryObject，将查询条件放入 update 中，返回条件字段列表
     */
    public static void injectUpdateConditions(Sql.Update update, Object queryObject) {

        if (queryObject instanceof Conditions) {
            ((Conditions) queryObject).getConditions().forEach(
                c -> injectCondition(update, c)
            );
        }

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        for (Field f : conditionFields) {
            Condition<?> c = Reflections.getFieldValue(queryObject, f);
            if (c == null) {
                continue;
            }
            injectCondition(update, c);
        }

    }

    ///////////////////////////////////////////////////////////////////

    private static void injectOrderBy(Sql.Select select, Collection<Condition<?>> conditions) {
        String orderBy = conditions.stream()
            .filter(c -> c != null && (c.getOrderAsc() != null || c.getOrderDesc() != null))
            .sorted(Comparator.comparing(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc()))
            .map(c -> c.getColumn() + (c.getOrderAsc() == null ? " desc" : " asc"))
            .collect(Collectors.joining(","));

        select.OrderBy(orderBy);
    }

    private static void injectCondition(Sql<?> sql, Condition<?> condition) {
        if (condition != null) {
            String columnName = condition.getColumn();
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
            if (condition.getNin() != null) {
                var value = condition.getNin();
                sql.And(columnName + " not in ?", value);
            }
        }
    }
}
