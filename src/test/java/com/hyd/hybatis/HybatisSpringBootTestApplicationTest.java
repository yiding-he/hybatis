package com.hyd.hybatis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class HybatisSpringBootTestApplicationTest {

    @Autowired
    protected Hybatis hybatis;

    @Test
    public void testInitialized() throws Exception {
        Assertions.assertNotNull(hybatis);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Sleep interrupted", e);
        }
    }
}
