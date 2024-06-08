package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.RawSqlFragment;
import com.hyd.hybatis.sql.SqlCommand;

@SuppressWarnings("unchecked")
public abstract class AbstractColumn<C extends AbstractColumn<C>> extends RawSqlFragment implements Column<C> {

    protected String alias;

    public C as(String alias) {
        this.alias = alias;
        return (C) this;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return super.toSqlFragment().append(appendAlias());
    }
}
