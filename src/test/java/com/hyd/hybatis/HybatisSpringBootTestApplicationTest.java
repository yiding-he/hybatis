package com.hyd.hybatis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HybatisSpringBootTestApplicationTest {

    @Autowired
    protected Hybatis hybatis;

    @Test
    public void testInitialized() throws Exception {
        Assertions.assertNotNull(hybatis);
    }
}
