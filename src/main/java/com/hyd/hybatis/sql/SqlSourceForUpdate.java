package com.hyd.hybatis.sql;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Bean;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Slf4j
public class SqlSourceForUpdate extends HybatisSqlSource {

    private final String[] key;

    public SqlSourceForUpdate(
        String sqlId, HybatisCore core, Configuration configuration,
        String tableName, Method mapperMethod, String[] key
    ) {
        super(sqlId, core, configuration, tableName, mapperMethod);
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
        SqlHelper.injectUpdateConditions(updateSql, query);

        if (update instanceof Map) {
            buildUpdateByMapObject(updateSql, (Map<String, Object>) update);
        } else {
            buildUpdateByBeanObject(updateSql, update);
        }

        log.debug("[{}]: {}", getSqlId(), updateSql.toCommand());
        return buildBoundSql(updateSql);
    }

    private Conditions buildConditionsFromKey(String[] key, Object parameterObject) {
        Conditions conditions = new Conditions();
        var camelToUnderline = getHybatisConfiguration().isCamelToUnderline();
        for (String k : key) {
            var columnName = camelToUnderline ? Str.camel2Underline(k) : k;
            var fieldName = camelToUnderline ? Str.underline2Camel(k) : k;
            conditions.with(columnName, c -> c.eq(Bean.getValue(parameterObject, fieldName)));
        }
        return conditions;
    }

    /**
     * 根据 Map 内容生成 Update 语句的 Set 部分
     */
    private void buildUpdateByMapObject(
        Sql.Update updateSql, Map<String, Object> update
    ) {
        var camelToUnderline = getHybatisConfiguration().isCamelToUnderline();
        update.forEach((field, value) -> {
            var columnName = camelToUnderline ? Str.camel2Underline(field) : field;
            updateSql.SetIfNotNull(columnName, value);
        });
    }

    /**
     * 根据 Java bean 对象生成 Update 语句的 Set 部分
     */
    private void buildUpdateByBeanObject(
        Sql.Update updateSql, Object update
    ) {

        List<Field> pojoFields = Reflections.getPojoFields(
            update.getClass(), getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        var camelToUnderline = getHybatisConfiguration().isCamelToUnderline();
        pojoFields.forEach(f -> {
            var columnName = Reflections.getColumnName(f, camelToUnderline);
            Object fieldValue = Reflections.getFieldValue(update, f);
            updateSql.SetIfNotNull(columnName, fieldValue);
        });
    }
}
