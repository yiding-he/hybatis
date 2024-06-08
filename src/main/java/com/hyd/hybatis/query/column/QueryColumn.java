package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Query;
import lombok.Getter;
import lombok.Setter;

/**
 * 表示这个列是从某个 Query 中选择来的
 */
@Getter @Setter
public class QueryColumn extends AbstractColumn<QueryColumn> {

    public QueryColumn() {

    }

    public QueryColumn(Query<?> from, String colName, String alias) {
        this.alias = alias;
        setSqlCommand(from.getAlias() + "." + colName);
    }
}
