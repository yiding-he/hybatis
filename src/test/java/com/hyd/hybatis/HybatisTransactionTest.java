package com.hyd.hybatis;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybatisTransactionTest extends HybatisSpringBootTestApplicationTest {

    public static void transactionTestFunction(Hybatis hybatis) throws SQLException {
        Supplier<Integer> count = () -> {
            try {
                return Integer.parseInt(String.valueOf(
                    hybatis.queryOne("select count(1) from test_simple").values().iterator().next()
                ));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        hybatis.execute("drop table if exists test_simple");
        hybatis.execute("create table test_simple(id int)");

        hybatis.runTransaction(() -> {
            hybatis.execute("insert into test_simple(id) values(1)");
            hybatis.execute("insert into test_simple(id) values(2)");
            hybatis.execute("insert into test_simple(id) values(3)");
        });
        assertEquals(3, count.get());

        try {
            hybatis.runTransaction(() -> {
                hybatis.queryList("select * from test_simple");
                hybatis.execute("insert into test_simple(id) values(4)");
                hybatis.execute("insert into test_simple(id) values(5)");
                hybatis.execute("insert into test_simple(id) values(6)");
                throw new RuntimeException("-------------");
            });
        } catch (HybatisException e) {
            System.err.println(e);
        }
        assertEquals(3, count.get());
    }

    @Test
    public void testRunTransaction() throws Exception {
        transactionTestFunction(hybatis);
    }
}
