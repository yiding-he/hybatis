package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

public class SqlSourceForInsert extends HybatisSqlSource {

    public SqlSourceForInsert(
        HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName
    ) {
        super(hybatisConfiguration, configuration, tableName);
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

        return buildBoundSql(insert);
    }
}