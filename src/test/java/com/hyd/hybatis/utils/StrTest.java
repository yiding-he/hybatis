package com.hyd.hybatis.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrTest {

    @Test
    public void testUnderline2Camel() throws Exception {
        assertEquals("HelloWorld", Str.underline2Camel("_hello_world"));
    }
}
