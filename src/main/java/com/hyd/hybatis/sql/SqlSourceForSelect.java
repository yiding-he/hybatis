package com.hyd.hybatis.sql;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisCore;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    /**
     * Determine what kind of content will be returned by this query.
     */
    protected final SelectMode selectMode;

    public SqlSourceForSelect(
        String sqlId, HybatisCore core, Configuration configuration, String tableName,
        SelectMode selectMode, Method mapperMethod
    ) {
        super(sqlId, core, configuration, tableName, mapperMethod);
        this.selectMode = selectMode;
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var context = new SqlHelper.Context(
            parameterObject, getTableName(), getHybatisConfiguration());

        Sql.Select select;
        if (parameterObject instanceof Conditions) {
            select = SqlHelper.buildSelectFromConditions(context);
        } else {
            select = SqlHelper.buildSelect(context);
        }

        if (selectMode == SelectMode.Count) {
            select.Columns("count(1)");
        }

        log.debug("[{}]: {}", getSqlId(), select.toCommand());
        return buildBoundSql(select);
    }

}
