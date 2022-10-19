package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    /**
     * 查询分页模式，非分页查询则为 None，否则为其他两种模式之一
     */
    protected final SelectMode selectMode;

    public SqlSourceForSelect(
        String sqlId, HybatisCore core, Configuration configuration, String tableName,
        SelectMode selectMode, Method mapperMethod
    ) {
        super(sqlId, core, configuration, tableName, mapperMethod);
        this.selectMode = selectMode;
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var context = new SqlHelper.Context(
            parameterObject, getTableName(), getHybatisConfiguration());

        Sql.Select select;
        if (parameterObject instanceof Conditions) {
            select = SqlHelper.buildSelectFromConditions(context);
        } else if (parameterObject instanceof Condition) {
            select = SqlHelper.buildSelectFromCondition(context);
        } else {
            select = SqlHelper.buildSelect(context);
        }

        if (selectMode == SelectMode.Count) {
            select.Columns("count(1)");
        } else {
            if (fields != null && fields.length > 0) {
                var columns = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    columns[i] = Str.camel2Underline(fields[i]);
                }
                select.Columns(columns);
            }
        }

        log.debug("[{}]: {}", getSqlId(), select.toCommand());
        return buildBoundSql(select);
    }

    /**
     * 创建一个同样查询条件，但只返回记录数的 SqlSource 对象
     */
    public SqlSourceForSelect asAnotherSelectMode(SelectMode selectMode) {
        var newSqlId = this.sqlId + "-" + selectMode.ordinal();
        SqlSourceForSelect result = new SqlSourceForSelect(
            newSqlId, this.core, this.configuration, this.tableName,
            selectMode, this.mapperMethod
        );
        result.setFields(this.fields);
        return result;
    }
}
