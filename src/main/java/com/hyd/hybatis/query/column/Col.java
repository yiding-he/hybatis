package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Query;

/**
 * 表示这个列是从某个 Query 中选择来的
 */
public class Col extends AbstractColumn<Col> {

    private Query<?> from;

    @Override
    public String getFrom() {
        return from.getAlias();
    }

    public Col() {

    }

    public Col(Query<?> from, String name, String alias) {
        this.from = from;
        this.name = name;
        this.alias = alias;
    }
}
