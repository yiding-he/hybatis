package com.hyd.hybatis.query.dataset;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.DataSet;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.query.column.AttributeExpression;
import com.hyd.hybatis.query.column.SimpleColumn;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableDataSet extends DataSet {

    private String name;

    public TableDataSet() {
        this.id = nextId();
    }

    public TableDataSet(String name) {
        this.name = name;
        this.id = nextId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSourceName() {
        if (alias != null && !alias.isEmpty()) {
            return name + " " + alias;
        }
        return name;
    }

    @Override
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

    @Override
    public TableDataSet as(String alias) {
        TableDataSet result = new TableDataSet(this.name);
        result.setAlias(alias);
        result.setId(this.id);
        result.setFilter(this.filter);
        result.setColumns(new ArrayList<>(this.columns));
        return result;
    }

    public Column col(String columnName) {
        AttributeExpression expr = new AttributeExpression(columnName);
        expr.setDataSetAlias(this.alias);
        expr.setDataSetId(this.id);
        return new SimpleColumn(expr);
    }

    @Override
    public DataSet select(Column... columns) {
        TableDataSet result = new TableDataSet(this.name);
        result.setFilter(this.filter);
        for (Column column : columns) {
            result.addColumn(column);
        }
        return result;
    }

    @Override
    public DataSet drop(Column... columns) {
        TableDataSet result = new TableDataSet(this.name);
        result.setFilter(this.filter);
        Set<Column> toDrop = new HashSet<>(List.of(columns));
        for (Column column : this.columns) {
            if (!toDrop.contains(column)) {
                result.addColumn(column);
            }
        }
        return result;
    }

    @Override
    public DataSet filter(Filter filter) {
        TableDataSet result = new TableDataSet(this.name);
        result.setColumns(new ArrayList<>(this.columns));
        result.setFilter(filter);
        return result;
    }
}
