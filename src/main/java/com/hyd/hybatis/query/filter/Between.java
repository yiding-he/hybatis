package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.Arrays;

public class Between<T> extends AbstractFilter {

    public Between(Column column, Object min, Object max) {
        setColumn(column);
        setValues(Arrays.asList(min, max));
    }

    public Between(Getter<T, ?> getter, Object min, Object max) {
        this(Column.prop(getter), min, max);
    }

    @Override
    public SqlCommand toSqlFragment() {
        var values = getValues();
        if (Obj.isEmpty(values)) {
            return null;
        }
        var command = new SqlCommand().append(getColumn().toSqlFragment());
        if (values.get(0) != null && values.get(1) != null) {
            return command
                .appendMaybeColumn(" between ?", values.get(0))
                .appendMaybeColumn(" and ?", values.get(1));
        } else if (values.get(0) != null) {
            return command.appendMaybeColumn(">= ?", values.get(0));
        } else {
            return command.appendMaybeColumn("<= ?", values.get(1));
        }
    }
}
