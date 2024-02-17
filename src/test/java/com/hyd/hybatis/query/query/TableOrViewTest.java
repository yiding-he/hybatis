package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Match.AND;
import static com.hyd.hybatis.query.Match.equal;

class TableOrViewTest extends HybatisSpringBootTestApplicationTest {

    @Test
    void testQuery1() throws Exception {
        TableOrView u = new TableOrView("DEPARTMENTS").as("d");
        u = u.matches(
            AND(
                equal(u.col("dept_no"), "d005"),
                equal(u.col("dept_name"), "Development")
            )
        ).columns(
            u.col("dept_no").as("DepartmentNumber")
        );

        hybatis
            .queryList(u.toSqlCommand())
            .forEach(System.out::println);
    }
}
