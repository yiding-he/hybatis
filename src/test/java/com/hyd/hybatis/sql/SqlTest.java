package com.hyd.hybatis.sql;

import com.hyd.hybatis.Conditions;
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

    @Test
    public void tstCTE() {
        var t2 = Sql
            .Select("id", "name", "price")
            .From("table2").injectConditions(
                new Conditions().withColumn("id").in(21, 22, 23)
            ).AsCTE("t2");

        var t3 = Sql
            .Select("id", "name", "price")
            .From("table3").injectConditions(
                new Conditions().withColumn("id").in(31, 32, 33)
            ).AsCTE("t3");

        var select = Sql.Select("t1.id", "t2.name", "t3.price")
            .From("t1")
            .Where("t1.id between ? and ?", 11, 19)
            .And("t1.type=?", "type1")
            .Ctes(t2, t3)
            .LeftJoin("t2 on t1.id=t2.id")
            .LeftJoin("t3 on t1.id=t3.id");

        System.out.println("select = " + select.toCommand());
    }
}
