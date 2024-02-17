package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;

@SuppressWarnings("unchecked")
public abstract class AbstractColumn<C extends AbstractColumn<C>> implements Column<C> {

    protected String alias;

    protected String name;

    public C name(String name) {
        this.name = name;
        return (C) this;
    }

    public C as(String alias) {
        this.alias = alias;
        return (C) this;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getName() {
        return name;
    }
}
