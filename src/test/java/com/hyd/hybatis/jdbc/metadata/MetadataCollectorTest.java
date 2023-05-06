package com.hyd.hybatis.jdbc.metadata;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

class MetadataCollectorTest {

    @Test
    public void testCollect() throws Exception {
        BasicDataSource  dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/employees");
        dataSource.setUsername("root");
        dataSource.setPassword("root123");

        MetadataCollector collector = new MetadataCollector(dataSource);
        var dbMeta = collector.collect();
        System.out.println(new JsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dbMeta));
    }
}