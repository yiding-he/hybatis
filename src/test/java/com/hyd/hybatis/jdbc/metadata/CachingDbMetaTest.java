package com.hyd.hybatis.jdbc.metadata;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

class CachingDbMetaTest {

    @Test
    public void testGetTable() throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:file:./.local/employees");

        CachingDbMeta cachingDbMeta = new CachingDbMeta(dataSource, 5000);
        var employees = cachingDbMeta.getTable("EMPLOYEES");
        System.out.println("employees.getName() = " + employees.getName());

        Thread.sleep(5500);
        employees = cachingDbMeta.getTable("EMPLOYEES");
    }
}