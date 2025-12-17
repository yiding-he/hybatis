package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.EntityUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropColumn<T> extends AbstractColumn<PropColumn<T>> {

    private String propName;

    public PropColumn(Getter<T, ?> getter) {
        this.propName = EntityUtil.resolveGetter(getter);
    }

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand(this.propName);
    }
}
