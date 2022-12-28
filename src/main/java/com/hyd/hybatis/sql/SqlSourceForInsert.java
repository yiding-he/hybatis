package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class SqlSourceForInsert extends HybatisSqlSource {

    public SqlSourceForInsert(
        String sqlId, HybatisCore core, Configuration configuration, String tableName, Method mapperMethod
    ) {
        super(sqlId, core, configuration, tableName, mapperMethod);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BoundSql build(Object insert) {

        var insertSql = Sql.Insert(getTableName());

        if (insert instanceof Map) {
            buildInsertByMapObject(insertSql, (Map<String, Object>) insert);
        } else {
            buildInsertByBeanObject(insertSql, insert);
        }

        log.debug("[{}]: {}", getSqlId(), insertSql.toCommand());
        return buildBoundSql(insertSql);
    }

    private void buildInsertByMapObject(Sql.Insert insertSql, Map<String, Object> insert) {
        var camelToUnderline = getHybatisConfiguration().isCamelToUnderline();
        insert.forEach((field, value) -> {
            if (value != null) {
                var columnName = camelToUnderline? Str.camel2Underline(field): field;
                insertSql.Values(columnName, value);
            }
        });
    }

    private void buildInsertByBeanObject(Sql.Insert insertSql, Object insert) {
        var fields = Reflections.getPojoFields(
            insert.getClass(),
            getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        var camelToUnderline = getHybatisConfiguration().isCamelToUnderline();
        for (Field field : fields) {
            var fieldValue = Reflections.getFieldValue(insert, field);
            if (fieldValue != null) {
                var columnName = Reflections.getColumnName(field, camelToUnderline);
                insertSql.Values(columnName, fieldValue);
            }
        }
    }
}
