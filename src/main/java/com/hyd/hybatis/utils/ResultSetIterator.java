package com.hyd.hybatis.utils;

import com.hyd.hybatis.HybatisException;
import com.hyd.hybatis.row.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetIterator implements Iterator<Row> {

    private final ResultSet resultSet;

    public ResultSetIterator(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean hasNext() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new HybatisException("Error fetching next entry from ResultSet", e);
        }
    }

    @Override
    public Row next() {
        try {
            return Row.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new HybatisException("Error parsing row data from from ResultSet", e);
        }
    }
}
