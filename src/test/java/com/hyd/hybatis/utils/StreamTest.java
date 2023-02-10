package com.hyd.hybatis.utils;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamTest {

    @Test
    public void testStreamAutoClose() throws Exception {
        var closed = new boolean[]{false};

        // 仅仅调用终结方法是不会令其关闭的
        var stream1 = Stream.of(1, 2, 3, 4, 5);
        stream1.onClose(() -> closed[0] = true).forEach(System.out::println);
        assertFalse(closed[0]);

        // 必须手工调用 close() 方法
        try(var stream2 = Stream.of(1, 2, 3, 4, 5)) {
            stream2.onClose(() -> closed[0] = true).forEach(System.out::println);
        }
        assertTrue(closed[0]);
    }
}
