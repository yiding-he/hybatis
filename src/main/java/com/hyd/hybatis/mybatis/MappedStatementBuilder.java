package com.hyd.hybatis.mybatis;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

/**
 * A simplified way of creating a MappedStatement for a select query.
 * The created MappedStatement is only for creating a ResultSetHandler.
 */
public class MappedStatementBuilder {

    public static MappedStatement newEmptySelectForEntity(Configuration configuration, Class<?> resultEntity) {
        var tmpId = UUID.randomUUID().toString().replace("-", "");
        var sqlSource = new SqlSourceBuilder(configuration).parse(null, null, Collections.emptyMap());
        var resultMap = new ResultMap.Builder(configuration, tmpId, resultEntity, emptyList(), true).build();
        return new MappedStatement.Builder(configuration, tmpId, sqlSource, SqlCommandType.SELECT)
            .resultMaps(List.of(resultMap)).build();
    }
}
