package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.*;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.EntityUtil;

/**
 * 表示字段。字段可以是三种情况之一：
 * 1. 来自某个表的字段；
 * 2. 常量值；
 * 3. 表达式。
 */
public interface Column extends Alias, SqlFragment {

    private static Column col(Object obj) {
        return obj == null ? lit(null) :
            obj instanceof Column ? (Column) obj : lit(obj);
    }

    //------------------------------------------------------

    static <T> PropColumn<T> prop(Getter<T, ?> getter) {
        return new PropColumn<>(getter);
    }

    static <T> QueryColumn from(Query query, Getter<T, ?> getter) {
        return new QueryColumn(query, EntityUtil.resolveGetter(getter), null);
    }

    static LitColumn lit(Object value) {
        return new LitColumn(value);
    }

    static ExpColumn exp(String statement, Object... params) {
        return new ExpColumn(statement, params);
    }

    static GroupConcatColumn groupConcat(Column column) {
        return new GroupConcatColumn(column);
    }

    static CaseColumn cases() {
        return new CaseColumn();
    }

    /// ///////////////////////

    static ExpColumn func1(String funcName, Column column) {
        var sqlCommand = new SqlCommand(funcName + "(")
            .append(column.toSqlFragment())
            .append(")");
        return new ExpColumn(sqlCommand);
    }

    static ExpColumn func2(String funcName, Column column1, Object value) {
        var sqlCommand = new SqlCommand(funcName + "(")
            .append(column1.toSqlFragment())
            .append(", ")
            .append(col(value).toSqlFragment())
            .append(")");
        return new ExpColumn(sqlCommand);
    }

    static ExpColumn count(Column column) {
        return func1("count", column);
    }

    static ExpColumn sum(Column column) {
        return func1("sum", column);
    }

    static ExpColumn avg(Column column) {
        return func1("avg", column);
    }

    static ExpColumn max(Column column) {
        return func1("max", column);
    }

    static ExpColumn min(Column column) {
        return func1("min", column);
    }

    static ExpColumn distinct(Column column) {
        return func1("distinct", column);
    }

    static ExpColumn ifNull(Column column, Object value) {
        return func2("ifnull", column, value);
    }

    static ExpColumn concat(Object... columnOrValues) {
        var sqlCommand = new SqlCommand("concat(");
        for (int i = 0, columnOrValuesLength = columnOrValues.length; i < columnOrValuesLength; i++) {
            Object obj = columnOrValues[i];
            sqlCommand.append(col(obj).toSqlFragment());
            if (i < columnOrValuesLength - 1) {
                sqlCommand.append(", ");
            }
        }
        sqlCommand.append(")");
        return new ExpColumn(sqlCommand);
    }

    //------------------------------------------------------

    default SqlCommand toSqlFragmentWithAlias() {
        return toSqlFragment().append(appendAlias());
    }
}
