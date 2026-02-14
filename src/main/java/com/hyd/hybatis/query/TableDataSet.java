package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableDataSet extends DataSet {

    private String name;

    public TableDataSet() {
    }

    public TableDataSet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSourceName() {
        return name;
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
