package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Slf4j
public class SqlSourceForUpdate extends HybatisSqlSource {

    private final String[] key;

    public SqlSourceForUpdate(
        String sqlId, HybatisConfiguration hybatisConfiguration, Configuration configuration,
        String tableName, String[] key
    ) {
        super(sqlId, hybatisConfiguration, configuration, tableName);
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BoundSql build(Object parameterObject) {
        Object query, update;
        if (parameterObject instanceof MapperMethod.ParamMap) {
            query = ((MapperMethod.ParamMap<?>) parameterObject).get("arg0");
            update = ((MapperMethod.ParamMap<?>) parameterObject).get("arg1");
        } else {
            // Use `key` instead of `query` object
            query = null;
            update = parameterObject;
        }

        Sql.Update updateSql = Sql.Update(getTableName());
        if (query != null) {
            SqlHelper.injectUpdateConditions(updateSql, query);
        } else {
            SqlHelper.injectUpdateConditionsByKey(updateSql, key, update);
        }

        if (update instanceof Map) {
            buildUpdateByMapObject(updateSql, (Map<String, Object>) update);
        } else {
            buildUpdateByBeanObject(updateSql, update);
        }

        log.info("[{}]: {}", getSqlId(), updateSql.toCommand());
        return buildBoundSql(updateSql);
    }

    private void buildUpdateByMapObject(Sql.Update updateSql, Map<String, Object> update) {
        update.forEach((field, value) -> {
            var columnName = Str.camel2Underline(field);
            updateSql.SetIfNotNull(columnName, value);
        });
    }

    private void buildUpdateByBeanObject(Sql.Update updateSql, Object update) {

        List<Field> pojoFields = Reflections.getPojoFields(
            update.getClass(), getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        for (Field f : pojoFields) {
            var columnName = Reflections.getColumnName(f);
            Object fieldValue = Reflections.getFieldValue(update, f);
            updateSql.SetIfNotNull(columnName, fieldValue);
        }
    }
}
