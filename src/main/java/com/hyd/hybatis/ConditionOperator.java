package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.utils.Str;
import org.springframework.util.Assert;

import java.util.Objects;

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

    //----------------------------------------------------

    public static class Between extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " BETWEEN ? AND ?", values[0], values[1]);
        }
    }

    public static class Gte extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return isNowConstant(values[0]) ?
                sql.And(column + " >= " + sql.getDialect().nowFunction()) :
                sql.And(column + " >= ?", values[0]);
        }
    }

    public static class Gt extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return isNowConstant(values[0]) ?
                sql.And(column + " > " + sql.getDialect().nowFunction()) :
                sql.And(column + " > ?", values[0]);
        }
    }

    public static class Lte extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return isNowConstant(values[0]) ?
                sql.And(column + " <= " + sql.getDialect().nowFunction()) :
                sql.And(column + " <= ?", values[0]);
        }
    }

    public static class Lt extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return isNowConstant(values[0]) ?
                sql.And(column + " < " + sql.getDialect().nowFunction()) :
                sql.And(column + " < ?", values[0]);
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
            return isNowConstant(values[0]) ?
                sql.And(column + " <> " + sql.getDialect().nowFunction()) :
                sql.And(column + " <> ?", values[0]);
        }
    }

    public static class Eq extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return isNowConstant(values[0]) ?
                sql.And(column + " = " + sql.getDialect().nowFunction()) :
                sql.And(column + " = ?", values[0]);
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

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            String firstValue = (values == null || values.length == 0) ? "0" : String.valueOf(values[0]);
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
            String firstValue = (values == null || values.length == 0) ? "0" : String.valueOf(values[0]);
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
            return sql.And(column + " LIKE ?", "%" + values[0] + "%");
        }
    }

    public static class EndsWith extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " LIKE ?", "%" + values[0]);
        }
    }

    public static class StartsWith extends ConditionOperator {

        @Override
        public Sql<?> operate(Sql<?> sql, String column, Object... values) {
            return sql.And(column + " LIKE ?", values[0] + "%");
        }
    }

}
