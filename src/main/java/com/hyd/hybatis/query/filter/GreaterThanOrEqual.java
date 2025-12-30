package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.Collections;

public class GreaterThanOrEqual<T> extends AbstractFilter {

    public GreaterThanOrEqual(Column column, Object value) {
        super(column, Collections.singletonList(value));
    }

    public GreaterThanOrEqual(Getter<T, ?> getter, Object value) {
        this(Column.prop(getter), value);
    }

    @Override
    public SqlCommand toSqlFragment() {
        var values = getValues();
        if (Obj.isEmpty(values)) {
            return null;
        }
        var value = values.get(0);
        var command = new SqlCommand().append(getColumn().toSqlFragment());
        return command.appendMaybeColumn(">=?", value);
    }
}
