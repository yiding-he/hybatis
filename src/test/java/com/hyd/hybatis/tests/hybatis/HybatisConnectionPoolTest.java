package com.hyd.hybatis.tests.hybatis;

import com.hyd.hybatis.Hybatis;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HybatisConnectionPoolTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private Hybatis hybatis;

    @Test
    public void testPoolBlocking() throws Exception {
        for (int i = 0; i < 50; i++) {
            hybatis.queryList("select * from DEPARTMENTS");
        }
    }
}
