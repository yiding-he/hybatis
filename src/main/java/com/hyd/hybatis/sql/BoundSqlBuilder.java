package com.hyd.hybatis.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

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

        return new BoundSql(configuration, statement, paramMappings, paramMap);
    }
}
