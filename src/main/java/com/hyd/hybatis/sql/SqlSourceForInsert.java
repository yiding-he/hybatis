package com.hyd.hybatis.sql;

import com.hyd.hybatis.reflection.Reflections;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

public class SqlSourceForInsert extends HybatisSqlSource {

    private final String tableName;

    public SqlSourceForInsert(Configuration configuration, String tableName) {
        super(configuration);
        this.tableName = tableName;
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var insert = Sql.Insert(tableName);
        var fields = Reflections.getPojoFields(parameterObject.getClass());

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
