package com.hyd.hybatis.query;

import com.hyd.hybatis.query.dataset.JoinCondition;
import com.hyd.hybatis.query.dataset.JoinDataSet;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DataSet {

    protected final List<Column> columns = new ArrayList<>();

    protected Filter filter;

    protected String alias;

    protected Long id;

    private static Long idCounter = 0L;

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

    public abstract DataSet as(String alias);

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected synchronized static Long nextId() {
        return ++idCounter;
    }

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
        return new JoinDataSet(this, other, joinType, new JoinCondition(leftCol, rightCol, JoinCondition.JoinOperator.EQUAL));
    }

    public DataSet join(DataSet other, Filter filter, JoinDataSet.JoinType joinType) {
        return new JoinDataSet(this, other, joinType, new JoinCondition(filter));
    }

    public DataSet join(DataSet other, Column leftCol, Column rightCol) {
        return join(other, leftCol, rightCol, JoinDataSet.JoinType.INNER);
    }

    public DataSet join(DataSet other, Filter filter) {
        return join(other, filter, JoinDataSet.JoinType.INNER);
    }

    public DataSet leftJoin(DataSet other, Column leftCol, Column rightCol) {
        return join(other, leftCol, rightCol, JoinDataSet.JoinType.LEFT);
    }

    public DataSet leftJoin(DataSet other, Filter filter) {
        return join(other, filter, JoinDataSet.JoinType.LEFT);
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
