package com.hyd.hybatis.jdbc.metadata;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class MetadataCollectorTest {

    @Test
    public void testCollect() throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:file:./.local/employees");

        MetadataCollector collector = new MetadataCollector(dataSource);
        System.out.println("Tables: " + collector.getAllTableNames());
        System.out.println("Views: " + collector.getAllViewNames());

        ////////////////////////////////////////

        var dbMeta = collector.collect();

        System.out.println("table:\n" +
                           dbMeta.getTables().stream()
                               .map(MetadataCollectorTest::dbTableToString)
                               .collect(Collectors.joining("\n")));

        System.out.println("view:\n" +
                           dbMeta.getViews().stream()
                               .map(MetadataCollectorTest::dbViewToString)
                               .collect(Collectors.joining("\n")));
    }

    private static String dbColumnToString(DbColumn column) {
        return column.getName() + "/" + column.getTypeName();
    }

    private static String dbTableToString(DbTable t) {
        return "  " + t.getName() + " : " +
               t.getColumns().stream()
                   .map(MetadataCollectorTest::dbColumnToString)
                   .collect(Collectors.toList());
    }

    private static String dbViewToString(DbView v) {
        return "  " + v.getName() + " : " +
               v.getColumns().stream()
                   .map(MetadataCollectorTest::dbColumnToString)
                   .collect(Collectors.toList());
    }
}
