package com.hyd.hybatis.sql;

import org.junit.jupiter.api.Test;

class SqlTest {

    @Test
    public void testSelect() throws Exception {
        Sql.Select select = Sql
            .Select("u.id", "u.name", "r.name")
            .Limit(10)
            .OrderBy("r.id desc")
            .From("user u")
            .LeftJoin("role r on r.id=u.role_id")
            .Where("u.id between ? and ?", 1, 1000);
        System.out.println("select.toCommand().getStatement() = " + select.toCommand().getStatement());
        System.out.println("select.toCommand().getParams() = " + select.toCommand().getParams());
    }

    @Test
    public void testSelectEmbedded() throws Exception {
        Sql.Select select = Sql
            .Select("u.id", "u.name")
            .From("user u")
            .Where("u.id in ", Sql
                .Select("user_id")
                .From("user_role")
                .Where("role_id in ?", 1, 2, 3)
            );
        System.out.println("select.toCommand().getStatement() = " + select.toCommand().getStatement());
        System.out.println("select.toCommand().getParams() = " + select.toCommand().getParams());
    }

    @Test
    public void testIf() throws Exception {
        int userId = 0;
        Sql.Select select = Sql
            .Select("u.id", "u.name")
            .From("user u")
            .Where("u.id=?", userId).If(userId > 0) // 如果 userId > 0，则生成这条 where 条件
            .And("u.active=?", "true");
        System.out.println("select.toCommand().getStatement() = " + select.toCommand().getStatement());
        System.out.println("select.toCommand().getParams() = " + select.toCommand().getParams());
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

    @Test
    public void testOnDuplicateIgnore() throws Exception {
        var insert = Sql.Insert("t1")
            .Values("id", 1)
            .Values("name", "2")
            .Values("price", "3.45")
            .OnDuplicateKeyIgnore();

        System.out.println("insert = " + insert.toCommand());
    }
}
