package com.hyd.hybatis.query.match;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.Collections;

public class Equal extends AbstractMatch {

    public Equal() {
    }

    public Equal(String field, Object value) {
        setField(field);
        setValue(value);
    }

    @Override
    public SqlCommand toSqlCommand() {
        var value = getValue();
        if (value == null) {
            return null;
        }
        return new SqlCommand(
            getField() + " = ?",
            Collections.singletonList(value)
        );
    }
}
