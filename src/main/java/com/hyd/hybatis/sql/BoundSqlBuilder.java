package com.hyd.hybatis.sql;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class BoundSqlBuilder {

    private final Configuration configuration;

    private final Sql<?> sql;

    public BoundSqlBuilder(Configuration configuration, Sql<?> sql) {
        this.configuration = configuration;
        this.sql = sql;
    }

    public BoundSql build() {
        var sqlCommand = sql.toCommand();
        var paramMappings = new ArrayList<ParameterMapping>();
        var paramMap = new HashMap<String, Object>();

        var statement = sqlCommand.getStatement();
        var params = sqlCommand.getParams();
        for (int i = 0; i < params.size(); i++) {
            var param = params.get(i);
            var paramKey = "param" + i;
            var paramMapping = new ParameterMapping.Builder(configuration, paramKey, param.getClass()).build();
            paramMappings.add(paramMapping);
            paramMap.put(paramKey, param);
        }

        log.info(sqlCommand.toString());
        BoundSql boundSql = new BoundSql(configuration, statement, paramMappings, paramMap);

        // 用于 MyBatis 构建查询缓存，否则的话 MyBatis 会尝试
        // 从查询条件对象中获取名为 'param0', 'param1' 等属性，然后失败
        paramMap.forEach(boundSql::setAdditionalParameter);

        return boundSql;
    }
}
