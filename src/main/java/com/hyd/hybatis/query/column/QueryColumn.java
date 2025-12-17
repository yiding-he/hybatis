package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.Setter;

/**
 * 表示这个列是从某个 Query 中选择来的
 */
@Getter @Setter
public class QueryColumn extends AbstractColumn<QueryColumn> {

    private Query from;

    private String colName;

    public QueryColumn() {

    }

    public QueryColumn(Query from, String colName, String alias) {
        this.from = from;
        this.colName = colName;
        this.alias = alias;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand(from.getAlias() + "." + colName);
    }
}
