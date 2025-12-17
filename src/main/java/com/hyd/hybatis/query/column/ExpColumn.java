package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

/**
 * 一段表达式（可带参数）作为一个列
 */
public class ExpColumn extends AbstractColumn<ExpColumn> {

    private SqlCommand sqlCommand;

    public ExpColumn() {
    }

    public ExpColumn(String statement, Object... params) {
        this.sqlCommand = new SqlCommand(statement, List.of(params));
    }

    public ExpColumn(SqlCommand sqlCommand) {
        this.sqlCommand = sqlCommand;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return this.sqlCommand;
    }
}
