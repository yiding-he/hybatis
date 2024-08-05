package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.ExpColumn;
import com.hyd.hybatis.query.column.LitColumn;
import com.hyd.hybatis.query.column.QueryColumn;
import com.hyd.hybatis.query.match.Equal;
import com.hyd.hybatis.sql.SqlCommand;

/**
 * 表示字段。字段可以是三种情况之一：
 * 1. 来自某个表的字段；
 * 2. 常量值；
 * 3. 表达式。
 */
public interface Column<C extends Column<C>> extends Alias, SqlFragment {

    static QueryColumn from(Query<?> query, String colName) {
        return new QueryColumn(query, colName, null);
    }

    static LitColumn lit(Object value) {
        return new LitColumn(value);
    }

    static ExpColumn exp(String statement, Object... params) {
        return new ExpColumn(statement, params);
    }

    //////////////////////////

    static ExpColumn count(Column<?> column) {
        var sqlCommand = new SqlCommand("count(")
            .append(column.toSqlFragment())
            .append(")");
        return new ExpColumn(sqlCommand);
    }

    //////////////////////////

    default Equal eq(Object value) {
        return new Equal(this, value);
    }
}
