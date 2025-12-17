package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

/**
 * 一段表达式（可带参数）作为一个列
 */
public class ExpColumn extends AbstractColumn<ExpColumn> {

    public ExpColumn() {
    }

    public ExpColumn(String statement, Object... params) {
        setSqlCommand(statement, params);
    }

    public ExpColumn(SqlCommand sqlCommand) {
        setSqlCommand(sqlCommand);
    }
}
