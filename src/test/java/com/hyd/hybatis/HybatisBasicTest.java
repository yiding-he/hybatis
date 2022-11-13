package com.hyd.hybatis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class HybatisBasicTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testQueryList() throws Exception {
        var rows = hybatis.queryList("select * from DEPARTMENTS");
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
    }
}
