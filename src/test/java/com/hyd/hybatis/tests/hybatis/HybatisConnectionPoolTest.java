package com.hyd.hybatis.tests.hybatis;

import com.hyd.hybatis.Hybatis;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybatisConnectionPoolTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private Hybatis hybatis;

    @Test
    public void testPoolBlocking() throws Exception {
        var start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            hybatis.runTransaction(() -> {
                hybatis.queryList("select * from DEPARTMENTS");
                sleep(1000);
            });
        }
        var duration = System.currentTimeMillis() - start;
        assertTrue(duration > 5000, "Finished too soon");
    }
}
