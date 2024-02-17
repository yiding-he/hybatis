package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.Collections;

public class Equal extends AbstractMatch {

    public Equal() {
    }

    public Equal(Projection projection, Object value) {
        setProjection(projection);
        setValue(value);
    }

    @Override
    public SqlCommand toSqlCommand() {
        var value = getValue();
        if (value == null) {
            return null;
        }
        return new SqlCommand(
            getProjection().toSqlExpression() + " = ?",
            Collections.singletonList(value)
        );
    }
}
