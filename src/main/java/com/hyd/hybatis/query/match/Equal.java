package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.Collections;

public class Equal extends AbstractMatch {

    public Equal() {
    }

    public Equal(Column<?> column, Object value) {
        setColumn(column);
        setValue(value);
    }

    @Override
    public SqlCommand toSqlFragment() {
        var value = getValue();
        if (value == null) {
            return null;
        }
        var command = new SqlCommand().append(getColumn().toSqlFragment());
        if (value instanceof Column<?>) {
            return command.append("=").append(((Column<?>) value).toSqlFragment());
        } else {
            return command.append("=?", Collections.singletonList(value));
        }
    }
}
