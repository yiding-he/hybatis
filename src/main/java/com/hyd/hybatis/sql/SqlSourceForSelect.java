package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    public SqlSourceForSelect(
        String sqlId, HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName
    ) {
        super(sqlId, hybatisConfiguration, configuration, tableName);
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

        var fields = getFields();
        if (fields != null && fields.length > 0) {
            var columns = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                columns[i] = Str.camel2Underline(fields[i]);
            }
            select.Columns(columns);
        }

        log.info("[{}]: {}", getSqlId(), select.toCommand());
        return buildBoundSql(select);
    }

}
