package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.sql.SqlCommand;

public class GroupConcatColumn extends AbstractColumn<GroupConcatColumn> {

    private final Column<?> column;

    private String separator;

    public GroupConcatColumn(Column<?> column) {
        this.column = column;
    }

    public GroupConcatColumn separator(String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public SqlCommand getSqlCommand() {
        var sqlCommand = new SqlCommand("group_concat(")
            .append(column.toSqlFragment());

        if (separator != null) {
            sqlCommand.append(" separator '" + separator + "'");
        }

        sqlCommand.append(")");
        return sqlCommand;
    }
}
