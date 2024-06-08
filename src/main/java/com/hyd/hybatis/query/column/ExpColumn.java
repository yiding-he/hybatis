package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

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
