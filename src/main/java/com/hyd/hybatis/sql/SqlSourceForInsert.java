package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

@Slf4j
public class SqlSourceForInsert extends HybatisSqlSource {

    public SqlSourceForInsert(
        String sqlId, HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName
    ) {
        super(sqlId, hybatisConfiguration, configuration, tableName);
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var insert = Sql.Insert(getTableName());
        var fields = Reflections.getPojoFields(
            parameterObject.getClass(),
            getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        for (Field field : fields) {
            var fieldValue = Reflections.getFieldValue(parameterObject, field);
            if (fieldValue != null) {
                var columnName = Reflections.getColumnName(field);
                insert.Values(columnName, fieldValue);
            }
        }

        log.info("[{}]: {}", getSqlId(), insert.toCommand());
        return buildBoundSql(insert);
    }
}
