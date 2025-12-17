package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.Collections;

public class Equal<T> extends AbstractMatch {

    public Equal(Column column, Object value) {
        super(column, Collections.singletonList(value));
    }

    public Equal(Getter<T, ?> getter, Object value) {
        this(Column.prop(getter), value);
    }

    @Override
    public SqlCommand toSqlFragment() {
        var values = getValues();
        if (Obj.isEmpty(values)) {
            return null;
        }
        var value = values.get(0);
        var command = new SqlCommand().append(getColumn().toSqlFragmentWithoutAlias());
        if (value instanceof Column) {
            return command.append("=").append(((Column) value).toSqlFragment());
        } else {
            return command.append("=?", Collections.singletonList(value));
        }
    }
}
