package com.hyd.hybatis;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class HybatisTransactionStandAloneTest {

    @Test
    public void testRunTransaction() throws Exception {
        var dataSource = new PooledDataSource(
            "org.h2.Driver", "jdbc:h2:file:./.local/employees", new Properties()
        );

        var hybatis = new Hybatis(dataSource);
        HybatisTransactionTest.transactionTestFunction(hybatis);
    }
}
