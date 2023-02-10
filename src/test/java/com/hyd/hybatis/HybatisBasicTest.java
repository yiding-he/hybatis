package com.hyd.hybatis;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybatisBasicTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testQueryList() throws Exception {
        var rows = hybatis.queryList("select * from DEPARTMENTS");
        assertFalse(rows.isEmpty());
        rows.forEach(System.out::println);
    }

    @Test
    public void testQueryStream() throws Exception {
        try (var stream = hybatis.queryStream("select * from DEPARTMENTS")) {
            stream.forEach(System.out::println);
        }
    }

    @Test
    public void testQueryWithConsumer() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        hybatis.query("select * from DEPARTMENTS", emptyList(), row -> counter.incrementAndGet());
        assertTrue(counter.get() > 0);
    }
}
