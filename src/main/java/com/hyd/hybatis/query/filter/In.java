package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;

import java.util.List;

import static com.hyd.hybatis.utils.Str.repeat;

public class In<T> extends AbstractFilter {

    public In(Column column, List<Object> values) {
        setColumn(column);
        setValues(values);
    }

    public In(Getter<T, ?> getter, List<Object> values) {
        this(Column.prop(getter), values);
    }

    @Override
    public SqlCommand toSqlFragment() {
        var values = getValues();
        if (Obj.isEmpty(values)) {
            return null;
        }

        var command = new SqlCommand().append(getColumn().toSqlFragment());
        return command.append(" IN (" + repeat("?", values.size(), ",") + ")", values);
    }
}
