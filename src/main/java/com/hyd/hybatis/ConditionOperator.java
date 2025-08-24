package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.hyd.hybatis.sql.Sql.isNowConstant;

/**
 * 查询条件操作符
 */
@FunctionalInterface
public interface ConditionOperator {

    Sql<?> operate(Sql<?> sql, String column, Object... values);

    ConditionOperator StartsWith = (sql, column, values) -> sql.And(column + " LIKE ?", values[0] + "%");

    ConditionOperator EndsWith = (sql, column, values) -> sql.And(column + " LIKE ?", "%" + values[0]);

    ConditionOperator Contains = (sql, column, values) -> sql.And(column + " LIKE ?", "%" + values[0] + "%");

    ConditionOperator Eq = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " = " + sql.getDialect().nowFunction()) :
        sql.And(column + " = ?", values[0]);

    ConditionOperator Ne = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " <> " + sql.getDialect().nowFunction()) :
        sql.And(column + " <> ?", values[0]);

    ConditionOperator Null = (sql, column, values) -> sql.And(column + " IS NULL");

    ConditionOperator NonNull = (sql, column, values) -> sql.And(column + " IS NOT NULL");

    ConditionOperator Lt = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " < " + sql.getDialect().nowFunction()) :
        sql.And(column + " < ?", values[0]);

    ConditionOperator Lte = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " <= " + sql.getDialect().nowFunction()) :
        sql.And(column + " <= ?", values[0]);

    ConditionOperator Gt = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " > " + sql.getDialect().nowFunction()) :
        sql.And(column + " > ?", values[0]);

    ConditionOperator Gte = (sql, column, values) -> isNowConstant(values[0]) ?
        sql.And(column + " >= " + sql.getDialect().nowFunction()) :
        sql.And(column + " >= ?", values[0]);

    ConditionOperator Between = (sql, column, values) -> sql.And(column + " BETWEEN ? AND ?", values[0], values[1]);

    ConditionOperator In = (sql, column, values) -> sql.And(column + " IN ?", values);

    ConditionOperator Nin = (sql, column, values) -> sql.And(column + " NOT IN ?", values);

    Map<String, ConditionOperator> VALUES = new HashMap<>(){{
        put("startsWith", StartsWith);
        put("endsWith", EndsWith);
        put("contains", Contains);
        put("eq", Eq);
        put("ne", Ne);
        put("null", Null);
        put("nonNull", NonNull);
        put("lt", Lt);
        put("lte", Lte);
        put("gt", Gt);
        put("gte", Gte);
        put("between", Between);
        put("in", In);
        put("nin", Nin);
    }};

    static ConditionOperator of(String operator) {
        operator = Introspector.decapitalize(operator);
        return VALUES.get(operator);
    }
}
