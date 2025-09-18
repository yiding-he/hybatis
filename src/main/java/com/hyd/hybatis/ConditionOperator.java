package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.utils.Str;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;

import static com.hyd.hybatis.sql.Sql.isNowConstant;

/**
 * 查询条件操作符
 */
public abstract class ConditionOperator {

    @SuppressWarnings("unused")
    public abstract Sql<?> operate(Sql<?> sql, String column, Object... values);

    public boolean matchName(String name) {
        return Objects.equals(name, this.getClass().getSimpleName());
    }

    protected <T> T firstValue(T defaultValue, Function<Object, T> converter, Object... values) {
        var list = valuesToList(values);
        return list.isEmpty() ? defaultValue : converter.apply(list.get(0));
    }

    protected Object firstValue(Object... values) {
        var list = valuesToList(values);
        return list.isEmpty() ? null : list.get(0);
    }

    protected List<Object> valuesToList(Object... values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return Collections.emptyList();
        } else if (values[0] instanceof Collection) {
            return new ArrayList<>((Collection<?>) values[0]);
        } else {
            return List.of(values[0]);
        }
    }

    //----------------------------------------------------

    public static class Between extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var list = valuesToList(values);
            Assert.isTrue(list.size() == 2, "Between operator requires two parameters.");
            return sql.And(column + " BETWEEN ? AND ?", list.get(0), list.get(1));
        }
    }

    public static class Gte extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " >= " + sql.getDialect().nowFunction()) :
                sql.And(column + " >= ?", firstValue);
        }
    }

    public static class Gt extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " > " + sql.getDialect().nowFunction()) :
                sql.And(column + " > ?", firstValue);
        }
    }

    public static class Lte extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " <= " + sql.getDialect().nowFunction()) :
                sql.And(column + " <= ?", firstValue);
        }
    }

    public static class Lt extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " < " + sql.getDialect().nowFunction()) :
                sql.And(column + " < ?", firstValue);
        }
    }

    public static class NonNull extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " IS NOT NULL");
        }
    }

    public static class Null extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " IS NULL");
        }
    }

    public static class Ne extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " <> " + sql.getDialect().nowFunction()) :
                sql.And(column + " <> ?", firstValue);
        }
    }

    public static class Eq extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue(values);
            return isNowConstant(firstValue) ?
                sql.And(column + " = " + sql.getDialect().nowFunction()) :
                sql.And(column + " = ?", firstValue);
        }

        public String getType() {
            return getClass().getSimpleName();
        }
    }

    public static class In extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " IN ?", values);
        }
    }

    public static class Nin extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " NOT IN ?", values);
        }
    }

    public static class OrderAsc extends ConditionOperator {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            String firstValue = firstValue("0", String::valueOf, values);
            Assert.isTrue(Str.isInteger(firstValue),
                "Value of parameter `orderAsc` should be an integer indicating its sorting order in the ORDER BY clause.");
            if (sql instanceof Sql.Select) {
                return ((Sql.Select) sql).OrderBy(column, true, Integer.parseInt(firstValue));
            } else {
                return sql;
            }
        }
    }

    public static class OrderDesc extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            String firstValue = firstValue("0", String::valueOf, values);
            Assert.isTrue(Str.isInteger(firstValue),
                "Value of parameter `orderDesc` should be an integer indicating its sorting order in the ORDER BY clause.");
            if (sql instanceof Sql.Select) {
                return ((Sql.Select) sql).OrderBy(column, false, Integer.parseInt(firstValue));
            } else {
                return sql;
            }
        }
    }

    public static class Contains extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue("", String::valueOf, values);
            return sql.And(column + " LIKE ?", "%" + firstValue + "%");
        }
    }

    public static class EndsWith extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue("", String::valueOf, values);
            return sql.And(column + " LIKE ?", "%" + firstValue);
        }
    }

    public static class StartsWith extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            var firstValue = firstValue("", String::valueOf, values);
            return sql.And(column + " LIKE ?", firstValue + "%");
        }
    }

}
