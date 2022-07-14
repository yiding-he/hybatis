package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbQuery;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.statement.MappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public abstract class AbstractMappedStatementFactory implements MappedStatementFactory {

    private LanguageDriver languageDriver = new HybatisLanguageDriver();

    private HybatisConfiguration hybatisConfiguration;

    public HybatisConfiguration getHybatisConfiguration() {
        return hybatisConfiguration;
    }

    public void setHybatisConfiguration(HybatisConfiguration hybatisConfiguration) {
        this.hybatisConfiguration = hybatisConfiguration;
    }

    public LanguageDriver getLanguageDriver() {
        return languageDriver;
    }

    public void setLanguageDriver(LanguageDriver languageDriver) {
        this.languageDriver = languageDriver;
    }

    /**
     * 构建不带返回值类型的 MappedStatement，例如 update/delete 语句
     *
     * @param configuration MyBatis 配置
     * @param sqlId         sqlId
     * @param sqlSource     包含本次执行上下文但尚未动态解析的 SQL 指令
     * @param commandType   执行类型
     *
     * @return 构建结果
     */
    protected MappedStatement buildMappedStatement(
        Configuration configuration, String sqlId, SqlSource sqlSource, SqlCommandType commandType
    ) {
        return buildMappedStatement(configuration, sqlId, null, sqlSource, commandType);
    }

    /**
     * 构建带返回值类型的 MappedStatement，例如 select 语句
     *
     * @param configuration MyBatis 配置
     * @param sqlId         sqlId
     * @param entityType    返回值类型
     * @param sqlSource     包含本次执行上下文但尚未动态解析的 SQL 指令
     * @param commandType   执行类型
     *
     * @return 构建结果
     */
    protected MappedStatement buildMappedStatement(
        Configuration configuration, String sqlId, Class<?> entityType, SqlSource sqlSource, SqlCommandType commandType
    ) {
        ResultMap resultMap = entityType == null ? null : new ResultMap
            .Builder(configuration, sqlId + "_RM", entityType, Collections.emptyList(), true)
            .build();

        return new MappedStatement
            .Builder(configuration, sqlId, sqlSource, commandType)
            .lang(new HybatisLanguageDriver())
            .resultMaps(resultMap == null ? new ArrayList<>() : Collections.singletonList(resultMap))
            .build();
    }

    protected String getTableName(Method method) {
        String tableName;

        if (method.isAnnotationPresent(HbQuery.class)) {
            tableName = method.getAnnotation(HbQuery.class).table();
        } else if (method.isAnnotationPresent(HbInsert.class)) {
            tableName = method.getAnnotation(HbInsert.class).table();
        } else if (method.isAnnotationPresent(HbUpdate.class)) {
            tableName = method.getAnnotation(HbUpdate.class).table();
        } else {
            throw new IllegalArgumentException("Method " + method.getName() + " contains no table information.");
        }

        if (tableName.length() > 7 && tableName.substring(0, 7).equalsIgnoreCase("select ")) {
            tableName = "(" + tableName + ") _tb_";
        }
        return tableName;
    }
}
