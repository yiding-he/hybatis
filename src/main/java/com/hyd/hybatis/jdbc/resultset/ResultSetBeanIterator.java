package com.hyd.hybatis.jdbc.resultset;

import com.hyd.hybatis.mybatis.ResultSetHandlerBuilder;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.session.Configuration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetBeanIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;

    public ResultSetBeanIterator(
        Configuration configuration, ResultSet resultSet, Class<T> entityClass
    ) throws SQLException {
        var resultSetHandler = ResultSetHandlerBuilder.newResultSetHandler(configuration, entityClass);
        var cursor = resultSetHandler.<T>handleCursorResultSets(resultSet.getStatement());
        this.iterator = cursor.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    public Stream<T> toBeanStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
}
