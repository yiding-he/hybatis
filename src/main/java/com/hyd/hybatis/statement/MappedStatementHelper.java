package com.hyd.hybatis.statement;

import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.sql.SelectMode;
import com.hyd.hybatis.sql.SqlSourceForSelect;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;

public class MappedStatementHelper {

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
    public static MappedStatement buildMappedStatement(
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
    public static MappedStatement buildMappedStatement(
        Configuration configuration, String sqlId, Class<?> entityType,
        SqlSource sqlSource, SqlCommandType commandType
    ) {

        ResultMap resultMap = null;
        if (sqlSource instanceof SqlSourceForSelect) {
            var selectMode = ((SqlSourceForSelect) sqlSource).getSelectMode();
            if (selectMode == SelectMode.Count) {
                resultMap = new ResultMap
                    .Builder(configuration, sqlId + "_RM", Long.class, Collections.emptyList(), true)
                    .build();
            }
        }

        if (resultMap == null) {
            resultMap = entityType == null ? null : new ResultMap
                .Builder(configuration, sqlId + "_RM", entityType, Collections.emptyList(), true)
                .build();
        }

        return new MappedStatement
            .Builder(configuration, sqlId, sqlSource, commandType)
            .lang(new HybatisLanguageDriver())
            .resultMaps(resultMap == null ? new ArrayList<>() : Collections.singletonList(resultMap))
            .build();
    }

    /**
     * 从现有的 MappedStatement 创建一个克隆对象，但使用新的 SqlSource 和新的 ResultMap
     */
    public static MappedStatement cloneWithNewSqlSourceAndResultMap(
        MappedStatement src, SqlSource newSource, ResultMap newResultMap, String suffix
    ) {
        return new MappedStatement
            .Builder(src.getConfiguration(), src.getId() + suffix, newSource, src.getSqlCommandType())
            .lang(new HybatisLanguageDriver())
            .resultMaps(newResultMap == null ? new ArrayList<>() : Collections.singletonList(newResultMap))
            .build();
    }

    /**
     * 从现有的 MappedStatement 创建一个克隆对象，但使用新的 SqlSource
     */
    public static MappedStatement cloneWithNewSqlSource(
        MappedStatement src, SqlSource newSource, String suffix
    ) {
        return new MappedStatement
            .Builder(src.getConfiguration(), src.getId() + suffix, newSource, src.getSqlCommandType())
            .lang(new HybatisLanguageDriver())
            .resultMaps(src.getResultMaps())
            .build();
    }
}
