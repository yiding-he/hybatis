package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Column.count;
import static com.hyd.hybatis.query.Match.equal;

class WrapTest extends HybatisSpringBootTestApplicationTest {


    @Test
    void test() throws Exception {
        TableOrView emp = new TableOrView("EMPLOYEES").as("e");
        emp = emp.matches(
            equal(emp.col("first_name"), "Abdelaziz")
        ).columns(
            emp.col("emp_no"),
            emp.col("gender")
        );

        Wrap wrap = new Wrap(emp).as("w");
        wrap = wrap.groupBy(
            wrap.col("gender")
        ).columns(
            count(wrap.col("emp_no")).as("emp_count")
        );

        hybatis
            .queryList(wrap.toSqlCommand())
            .forEach(System.out::println);
    }
}
