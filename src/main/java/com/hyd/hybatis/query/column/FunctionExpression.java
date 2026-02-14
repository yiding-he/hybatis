package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.Arrays;
import java.util.List;

public class FunctionExpression extends Expression {

    private final String funcName;
    private final List<Expression> args;

    public FunctionExpression(String funcName, Expression... args) {
        this.funcName = funcName;
        this.args = Arrays.asList(args);
    }

    @Override
    public SqlCommand toSqlCommand() {
        var cmd = new SqlCommand(funcName + "(");
        for (int i = 0; i < args.size(); i++) {
            cmd.append(args.get(i).toSqlCommand());
            if (i < args.size() - 1) {
                cmd.append(", ");
            }
        }
        cmd.append(")");
        return cmd;
    }
}
