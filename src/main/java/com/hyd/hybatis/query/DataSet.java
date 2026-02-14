package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.JoinCondition;
import com.hyd.hybatis.query.column.JoinCondition.JoinOperator;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DataSet {

    protected final List<Column> columns = new ArrayList<>();

    protected Filter filter;

    public List<Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public abstract DataSet select(Column... columns);

    public abstract DataSet drop(Column... columns);

    public abstract DataSet filter(Filter filter);

    public abstract String getSourceName();

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setColumns(List<Column> columns) {
        this.columns.clear();
        this.columns.addAll(columns);
    }

    public DataSet join(DataSet other, Column leftCol, Column rightCol, JoinDataSet.JoinType joinType) {
        return new JoinDataSet(this, other, joinType, new JoinCondition(leftCol, rightCol, JoinOperator.EQUAL));
    }

    public SqlCommand toSqlCommand() {
        var cmd = new SqlCommand();
        cmd.append("SELECT ");
        if (columns.isEmpty()) {
            cmd.append("*");
        } else {
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    cmd.append(", ");
                }
                cmd.append(columns.get(i).getExpression().toSqlCommand());
            }
        }
        cmd.append(" FROM ").append(getSourceName());

        if (filter != null) {
            cmd.append(" WHERE ").append(filter.toSqlCommand());
        }

        return cmd;
    }

    protected static Set<Column> toSet(Column... columns) {
        return new HashSet<>(List.of(columns));
    }
}
