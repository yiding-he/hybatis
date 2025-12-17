package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import lombok.Getter;

public abstract class AbstractColumn<C extends AbstractColumn<C>> implements Column {

    @Getter
    protected String alias;

    @SuppressWarnings("unchecked")
    public C as(String alias) {
        this.alias = alias;
        return (C) this;
    }
}
