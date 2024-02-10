package com.hyd.hybatis.mybatis;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

/**
 * 利用 MyBatis 机制将 ResultSet 转换为 JavaBean
 */
public class ResultSetHandlerBuilder {

    public static ResultSetHandler newResultSetHandler(Configuration configuration, Class<?> entityClass) {
        var mappedStatement = MappedStatementBuilder.newEmptySelectForEntity(configuration, entityClass);
        return configuration.newResultSetHandler(null, mappedStatement, RowBounds.DEFAULT, null, null, null);
    }
}
