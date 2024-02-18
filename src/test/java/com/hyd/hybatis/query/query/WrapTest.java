package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Aggregate.count;
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
        wrap = wrap.aggregates(
            count(wrap.col("emp_no")).as("emp_count")
        ).columns(
            wrap.col("gender")
        );

        System.out.println(wrap.toSqlCommand());
    }
}
