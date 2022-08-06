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

    public static Sql.Select buildSelectFromConditions(Context context) {
        Conditions conditions = (Conditions) context.paramObject;
        Sql.Select select = new Sql.Select("*").From(context.tableName);
        for (Condition<?> condition : conditions.getConditions()) {
            injectCondition(select, condition);
        }
        injectOrderBy(select, conditions.getConditions());
        return select;
    }

    public static Sql.Select buildSelectFromCondition(Context context) {
        Condition<?> condition = (Condition<?>) context.paramObject;
        Sql.Select select = new Sql.Select("*").From(context.tableName);
        injectCondition(select, condition);
        injectOrderBy(select, Collections.singletonList(condition));
        return select;
    }

    public static Sql.Select buildSelect(Context context) {
        var tableName = context.tableName;
        var queryObject = context.paramObject;
        var select = Sql.Select("*").From(tableName);

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        var conditionMappings = new HashMap<Field, Condition<?>>();
        for (Field f : conditionFields) {
            Condition<?> condition = Reflections.getFieldValue(queryObject, f);
            if (condition == null) {
                continue;
            }

            var columnName = Reflections.getColumnName(f);
            condition.setColumn(columnName);
            conditionMappings.put(f, condition);
        }

        for (Field conditionField : conditionFields) {
            var condition = conditionMappings.get(conditionField);
            injectCondition(select, condition);
        }

        var conditions = conditionMappings.values();
        injectOrderBy(select, conditions);
        return select;
    }

    public static void injectUpdateConditions(Sql.Update update, Object queryObject) {

        if (queryObject instanceof Conditions) {
            ((Conditions) queryObject).getConditions().forEach(c -> injectCondition(update, c));
            return;
        }

        var conditionFields = Reflections
            .getPojoFieldsOfType(queryObject.getClass(), Condition.class, Collections.emptyList());

        for (Field f : conditionFields) {
            Condition<?> condition = Reflections.getFieldValue(queryObject, f);
            if (condition == null) {
                continue;
            }

            injectCondition(update, condition);
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
        }
    }
}
