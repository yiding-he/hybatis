package com.hyd.hybatis.statement;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class InsertMappedStatementFactory implements MappedStatementFactory {

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        return null;// TODO implement com.hyd.hybatis.statement.InsertMappedStatementFactory.createMappedStatement()
    }
}
