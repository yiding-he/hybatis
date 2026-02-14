package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.sql.SqlCommand;

public class BinaryFilter implements Filter {

    private final Column left;
    private final Column right;
    private final String operator;

    public BinaryFilter(Column left, Column right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public SqlCommand toSqlCommand() {
        var cmd = new SqlCommand();
        cmd.append(left.getExpression().toSqlCommand());
        cmd.append(" ").append(operator).append(" ");
        cmd.append(right.getExpression().toSqlCommand());
        return cmd;
    }
}
