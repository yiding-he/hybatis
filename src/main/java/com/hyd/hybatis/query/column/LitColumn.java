package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

/**
 * 表示这个列是一个值，在生成 SQL 时直接作为一个占位符
 */
public class LitColumn extends AbstractColumn<LitColumn> {

    private Object value;

    public LitColumn() {
    }

    public LitColumn(Object value) {
        this.value = value;
    }

    @Override
    public SqlCommand toSqlFragment() {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return new SqlCommand(String.valueOf(value));
        } else {
            return new SqlCommand("?", List.of(value));
        }
    }
}
