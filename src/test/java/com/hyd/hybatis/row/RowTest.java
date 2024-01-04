package com.hyd.hybatis.row;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class RowTest {

    @Test
    public void testGetDate() throws Exception {
        Row row = new Row().putValue("date", LocalDateTime.now());
        System.out.println("row.getDate(\"date\") = " + row.getDate("date"));
    }
}
