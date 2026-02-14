package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.JoinCondition;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JoinDataSet extends DataSet {

    private final DataSet left;

    private final DataSet right;

    private final JoinCondition joinCondition;

    private final JoinType joinType;

    public enum JoinType {
        INNER, LEFT, RIGHT, CROSS
    }

    public JoinDataSet(
        DataSet left, DataSet right,
        JoinType joinType,
        JoinCondition joinCondition
    ) {
        this.left = left;
        this.right = right;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
    }

    public DataSet getLeft() {
        return left;
    }

    public DataSet getRight() {
        return right;
    }

    public JoinCondition getJoinCondition() {
        return joinCondition;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    public String getSourceName() {
        return "(" + left.getSourceName() + " " + right.getSourceName() + ")";
    }

    @Override
    public DataSet select(Column... columns) {
        JoinDataSet result = new JoinDataSet(this.left, this.right, this.joinType, this.joinCondition);
        result.setFilter(this.filter);
        for (Column column : columns) {
            result.addColumn(column);
        }
        return result;
    }

    @Override
    public DataSet drop(Column... columns) {
        JoinDataSet result = new JoinDataSet(this.left, this.right, this.joinType, this.joinCondition);
        result.setFilter(this.filter);
        HashSet<Column> toDrop = new HashSet<>(List.of(columns));
        for (Column column : this.columns) {
            if (!toDrop.contains(column)) {
                result.addColumn(column);
            }
        }
        return result;
    }

    @Override
    public DataSet filter(Filter filter) {
        JoinDataSet result = new JoinDataSet(this.left, this.right, this.joinType, this.joinCondition);
        result.setColumns(new ArrayList<>(this.columns));
        result.setFilter(filter);
        return result;
    }

    @Override
    public SqlCommand toSqlCommand() {
        var cmd = new SqlCommand();

        if (left instanceof JoinDataSet) {
            JoinDataSet leftJoin = (JoinDataSet) left;
            cmd.append(leftJoin.toSqlCommand());
        } else {
            cmd.append("SELECT * FROM ").append(left.getSourceName());
        }

        String joinTypeStr = switch (joinType) {
            case INNER -> " INNER JOIN ";
            case LEFT -> " LEFT JOIN ";
            case RIGHT -> " RIGHT JOIN ";
            case CROSS -> " CROSS JOIN ";
        };

        if (right instanceof JoinDataSet) {
            JoinDataSet rightJoin = (JoinDataSet) right;
            cmd.append(joinTypeStr).append(rightJoin.toSqlCommand());
        } else {
            cmd.append(joinTypeStr).append(right.getSourceName());
        }

        cmd.append(" ON ").append(joinCondition.toSqlCommand());

        if (filter != null) {
            cmd.append(" WHERE ").append(filter.toSqlCommand());
        }

        return cmd;
    }
}
