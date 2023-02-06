package com.hyd.hybatis.sql;

import org.junit.jupiter.api.Test;

class SqlTest {

    @Test
    public void testSelect() throws Exception {
        Sql.Select select = Sql.Select("*").From("table1").Where("id=#{id}");
        System.out.println(select.getSql());
    }

    @Test
    public void testOnDuplicateUpdate() throws Exception {
        var insert = Sql.Insert("t1")
            .Values("id", 1)
            .Values("name", "2")
            .Values("price", "3.45")
            .OnDuplicateKeyUpdate("name", "price");

        System.out.println("insert = " + insert.toCommand());
    }
}
