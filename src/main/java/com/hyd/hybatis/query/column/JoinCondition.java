package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.sql.SqlCommand;

public class JoinCondition {

    private final Column left;

    private final Column right;

    private final JoinOperator operator;

    public JoinCondition(Column left, Column right, JoinOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Column getLeft() {
        return left;
    }

    public Column getRight() {
        return right;
    }

    public JoinOperator getOperator() {
        return operator;
    }

    public SqlCommand toSqlCommand() {
        var leftCmd = left.getExpression().toSqlCommand();
        var opStr = switch (operator) {
            case EQUAL -> " = ";
            case NOT_EQUAL -> " != ";
            case GREATER_THAN -> " > ";
            case LESS_THAN -> " < ";
            case GREATER_OR_EQUAL -> " >= ";
            case LESS_OR_EQUAL -> " <= ";
            case IS_NULL -> " IS NULL";
            case IS_NOT_NULL -> " IS NOT NULL";
            case AND -> " AND ";
            case OR -> " OR ";
            case NOT -> " NOT ";
        };

        var cmd = new SqlCommand().append(leftCmd).append(opStr);
        if (right != null && (operator == JoinOperator.AND || operator == JoinOperator.OR || operator == JoinOperator.NOT)) {
            cmd.append(right.getExpression().toSqlCommand());
        } else if (right != null) {
            cmd.append(right.getExpression().toSqlCommand());
        }
        return cmd;
    }

    public enum JoinOperator {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_OR_EQUAL,
        LESS_OR_EQUAL,
        IS_NULL,
        IS_NOT_NULL,
        AND,
        OR,
        NOT
    }
}
