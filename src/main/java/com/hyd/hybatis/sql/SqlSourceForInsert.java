package com.hyd.hybatis.sql;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class SqlSourceForInsert extends HybatisSqlSource {

    public SqlSourceForInsert(
        String sqlId, HybatisConfiguration hybatisConfiguration, Configuration configuration, String tableName
    ) {
        super(sqlId, hybatisConfiguration, configuration, tableName);
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

        log.info("[{}]: {}", getSqlId(), insertSql.toCommand());
        return buildBoundSql(insertSql);
    }

    private void buildInsertByMapObject(Sql.Insert insertSql, Map<String, Object> insert) {
        insert.forEach((field, value) -> {
            if (value != null) {
                var columnName = Str.camel2Underline(field);
                insertSql.Values(columnName, value);
            }
        });
    }

    private void buildInsertByBeanObject(Sql.Insert insertSql, Object insert) {
        var fields = Reflections.getPojoFields(
            insert.getClass(),
            getHybatisConfiguration().getHideBeanFieldsFrom()
        );

        for (Field field : fields) {
            var fieldValue = Reflections.getFieldValue(insert, field);
            if (fieldValue != null) {
                var columnName = Reflections.getColumnName(field);
                insertSql.Values(columnName, fieldValue);
            }
        }
    }
}
