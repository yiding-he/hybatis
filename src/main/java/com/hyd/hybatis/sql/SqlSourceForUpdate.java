package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.List;

public class SqlSourceForUpdate extends HybatisSqlSource {

    public SqlSourceForUpdate(HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName) {
        super(hybatisConfiguration, configuration, tableName);
    }

    @Override
    protected BoundSql build(Object parameterObject) {
        Object query, update;
        if (parameterObject instanceof MapperMethod.ParamMap) {
            query = ((MapperMethod.ParamMap<?>) parameterObject).get("arg0");
            update = ((MapperMethod.ParamMap<?>) parameterObject).get("arg1");
        } else {
            throw new IllegalArgumentException("Invalid parameter type: " + parameterObject.getClass());
        }

        Sql.Update updateSql = Sql.Update(getTableName());
        SqlHelper.injectUpdateConditions(updateSql, query);

        List<Field> pojoFields = Reflections.getPojoFields(
            update.getClass(), getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        for (Field f : pojoFields) {
            var columnName = Reflections.getColumnName(f);
            Object fieldValue = Reflections.getFieldValue(update, f);
            updateSql.SetIfNotNull(columnName, fieldValue);
        }

        return buildBoundSql(updateSql);
    }
}
