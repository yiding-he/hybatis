package com.hyd.hybatis.sql;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Bean;
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
        String sqlId, HybatisCore core, Configuration configuration,
        String tableName, String[] key
    ) {
        super(sqlId, core, configuration, tableName);
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
            query = buildConditionsFromKey(key, parameterObject);
            update = parameterObject;
        }

        Sql.Update updateSql = Sql.Update(getTableName());
        var conditionColumns = SqlHelper.injectUpdateConditions(updateSql, query);

        if (update instanceof Map) {
            buildUpdateByMapObject(updateSql, conditionColumns, (Map<String, Object>) update);
        } else {
            buildUpdateByBeanObject(updateSql, conditionColumns, update);
        }

        log.info("[{}]: {}", getSqlId(), updateSql.toCommand());
        return buildBoundSql(updateSql);
    }

    private Conditions buildConditionsFromKey(String[] key, Object parameterObject) {
        Conditions conditions = new Conditions();
        for (String k : key) {
            var columnName = Str.camel2Underline(k);
            var fieldName = Str.underline2Camel(k);
            conditions.with(columnName, c -> c.eq(Bean.getValue(parameterObject, fieldName)));
        }
        return conditions;
    }

    private void buildUpdateByMapObject(
        Sql.Update updateSql, List<String> conditionColumns, Map<String, Object> update
    ) {
        update.forEach((field, value) -> {
            var columnName = Str.camel2Underline(field);
            if (conditionColumns.contains(columnName)) {
                return;
            }
            updateSql.SetIfNotNull(columnName, value);
        });
    }

    private void buildUpdateByBeanObject(
        Sql.Update updateSql, List<String> conditionColumns, Object update
    ) {

        List<Field> pojoFields = Reflections.getPojoFields(
            update.getClass(), getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        pojoFields.forEach(f -> {
            var columnName = Reflections.getColumnName(f);
            if (conditionColumns.contains(columnName)) {
                return;
            }
            Object fieldValue = Reflections.getFieldValue(update, f);
            updateSql.SetIfNotNull(columnName, fieldValue);
        });
    }
}
