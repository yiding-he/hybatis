package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.utils.Str;
import org.springframework.util.Assert;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hyd.hybatis.ConditionOperator.CustomOperatorParserHolder.getCustomOperatorParser;
import static com.hyd.hybatis.sql.Sql.isNowConstant;

/**
 * 查询条件操作符
 */
@FunctionalInterface
public interface ConditionOperator {

    @SuppressWarnings("unused")
    Sql<?> operate(Sql<?> sql, String column, Object... values);

    ConditionOperator INVALID = (sql, column, values) -> {
        throw new IllegalStateException("`INVALID` operator should be ignored.");
    };

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

    ConditionOperator OrderAsc = (sql, column, values) -> {
        String firstValue = (values == null || values.length == 0) ? "0" : String.valueOf(values[0]);
        Assert.isTrue(Str.isInteger(firstValue),
            "Value of parameter `orderAsc` should be an integer indicating its sorting order in the ORDER BY clause.");
        if (sql instanceof Sql.Select) {
            return ((Sql.Select) sql).OrderBy(column, true, Integer.parseInt(firstValue));
        } else {
            return sql;
        }
    };

    ConditionOperator OrderDesc = (sql, column, values) -> {
        String firstValue = (values == null || values.length == 0) ? "0" : String.valueOf(values[0]);
        Assert.isTrue(Str.isInteger(firstValue),
            "Value of parameter `orderDesc` should be an integer indicating its sorting order in the ORDER BY clause.");
        if (sql instanceof Sql.Select) {
            return ((Sql.Select) sql).OrderBy(column, false, Integer.parseInt(firstValue));
        } else {
            return sql;
        }
    };

    Map<String, ConditionOperator> VALUES = new HashMap<>() {
        private static final long serialVersionUID = 8311002634730524089L;

        {
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
            put("orderAsc", OrderAsc);
            put("orderDesc", OrderDesc);
        }
    };

    @FunctionalInterface
    interface OperatorParser {

        /**
         * 根据操作符名称和条件值，返回对应的操作符常量。
         *
         * @param operator 操作符名称
         * @param values   条件值
         */
        ConditionOperator parse(String operator, String... values);
    }

    OperatorParser DEFAULT_OPERATOR_PARSER = (operator, values) -> {
        operator = Introspector.decapitalize(operator);
        var firstValue = (values == null || values.length == 0) ? null : values[0];

        // 针对是否为空的条件解析：当条件操作符为 null 时，根据条件值返回对应的操作符，
        // 如果值为空则返回 INVALID。
        if (operator.equals("null")) {
            return Objects.equals(firstValue, "true") ? Null :
                Objects.equals(firstValue, "false") ? NonNull : INVALID;
        }

        return VALUES.getOrDefault(operator, INVALID);
    };

    /**
     * 允许用户添加自定义的操作符解析器。仅当默认的解析器无法处理时，才会调用它。
     */
    class CustomOperatorParserHolder {

        private static OperatorParser customOperatorParser = null;

        public static void setCustomOperatorParser(OperatorParser customOperatorParser) {
            CustomOperatorParserHolder.customOperatorParser = customOperatorParser;
        }

        public static OperatorParser getCustomOperatorParser() {
            return customOperatorParser;
        }
    }

    static ConditionOperator of(String operator, String... values) {
        var conditionOperator = DEFAULT_OPERATOR_PARSER.parse(operator, values);
        return conditionOperator == INVALID && getCustomOperatorParser() != null ?
            getCustomOperatorParser().parse(operator, values) :
            conditionOperator;
    }

    //-------------------------- 用于二次开发的方法 --------------------------

    /**
     * 注册一个新的操作符
     */
    static void registerOperator(String operator, ConditionOperator conditionOperator) {
        VALUES.put(operator, conditionOperator);
    }

    /**
     * 针对新的操作符，你可能想要自定义操作符解析器，这时你可以调用此方法来覆盖默认的解析器。
     */
    static void overrideOperatorParser(OperatorParser operatorParser) {
        CustomOperatorParserHolder.setCustomOperatorParser(operatorParser);
    }
}
