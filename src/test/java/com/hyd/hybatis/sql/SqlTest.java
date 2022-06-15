package com.hyd.hybatis.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlTest {

    @Test
    public void testSelect() throws Exception {
        Sql.Select select = Sql.Select("*").From("table1").Where("id=#{id}");
        System.out.println(select.getSql());
    }
}
