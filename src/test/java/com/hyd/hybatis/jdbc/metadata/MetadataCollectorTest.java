package com.hyd.hybatis.jdbc.metadata;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class MetadataCollectorTest {

    private static final ObjectWriter WRITER = new JsonMapper().writerWithDefaultPrettyPrinter();

    @Test
    public void testCollect() throws Exception {
        BasicDataSource  dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:file:./.local/employees");

        MetadataCollector collector = new MetadataCollector(dataSource);
        var dbMeta = collector.collect();

        var tableNames = dbMeta.getTables().stream().map(DbTable::getName).collect(Collectors.toList());
        System.out.println("tableNames = " + tableNames);

        var viewNames = dbMeta.getViews().stream().map(DbView::getName).collect(Collectors.toList());
        System.out.println("viewNames = " + viewNames);

        System.out.println(WRITER.writeValueAsString(dbMeta.getViews()));
    }
}