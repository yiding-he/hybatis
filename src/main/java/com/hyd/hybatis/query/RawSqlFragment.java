package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;

import java.util.List;

@Getter
public class RawSqlFragment implements SqlFragment {

    private SqlCommand sqlCommand;

    public RawSqlFragment() {
    }

    public RawSqlFragment(String statement, Object... params) {
        setSqlCommand(new SqlCommand(statement, List.of(params)));
    }

    public void setSqlCommand(SqlCommand sqlCommand) {
        this.sqlCommand = sqlCommand;
    }

    public void setSqlCommand(String statement, Object... params) {
        this.sqlCommand = new SqlCommand(statement, List.of(params));
    }

    public SqlCommand getSqlCommand() {
        return this.sqlCommand;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return this.getSqlCommand();
    }
}
