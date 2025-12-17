package com.hyd.hybatis.query.column;

/**
 * 表示这个列是一个值，在生成 SQL 时直接作为一个占位符
 */
public class LitColumn extends AbstractColumn<LitColumn> {

    public LitColumn() {
    }

    public LitColumn(Object value) {
        setValue(value);
    }

    public void setValue(Object value) {
        if (value == null) {
            setSqlCommand(null);
        } else if (value instanceof Number) {
            setSqlCommand(String.valueOf(value));
        } else {
            setSqlCommand("?", value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        var sqlCommand = getSqlCommand();
        if (sqlCommand == null) {
            return null;
        }
        var value = sqlCommand.getParams();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (T) value.get(0);
    }
}
