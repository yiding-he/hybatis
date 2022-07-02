package com.hyd.hybatis.statement;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class DeleteMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        return getSqlCommandType(method) == SqlCommandType.DELETE;
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        return null;// TODO implement com.hyd.hybatis.statement.DeleteMappedStatementFactory.createMappedStatement()
    }
}
