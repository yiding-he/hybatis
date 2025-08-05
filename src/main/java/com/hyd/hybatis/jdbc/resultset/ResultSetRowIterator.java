package com.hyd.hybatis.jdbc.resultset;

import com.hyd.hybatis.HybatisException;
import com.hyd.hybatis.row.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetRowIterator implements Iterator<Row> {

    private final ResultSet resultSet;

    public ResultSetRowIterator(ResultSet resultSet) {
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

    public Stream<Row> toRowStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
    }
}
