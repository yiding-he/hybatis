package com.hyd.hybatis.statement;

import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.sql.InsertSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

public class InsertMappedStatementFactory extends AbstractMappedStatementFactory {

    @Override
    public boolean match(Method method) {
        return getSqlCommandType(method) == SqlCommandType.INSERT && method.getParameterCount() == 1;
    }

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String sqlId, Method method) {
        return new MappedStatement
            .Builder(configuration, sqlId, new InsertSqlSource(configuration), SqlCommandType.SELECT)
            .lang(new HybatisLanguageDriver())
            .build();

    }
}
