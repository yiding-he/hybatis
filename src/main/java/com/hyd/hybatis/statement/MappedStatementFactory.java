package com.hyd.hybatis.statement;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public interface MappedStatementFactory {

    MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method);
}
