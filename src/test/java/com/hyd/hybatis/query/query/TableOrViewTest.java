package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Match.AND;
import static com.hyd.hybatis.query.Match.equal;

class TableOrViewTest extends HybatisSpringBootTestApplicationTest {

    @Test
    void testQuery1() throws Exception {
        TableOrView d = new TableOrView("DEPARTMENTS").as("d");
        d = d.matches(
            AND(
                equal(d.col("dept_no"), "d005"),
                equal(d.col("dept_name"), "Development")
            )
        ).columns(
            d.col("dept_no").as("DepartmentNumber")
        ).limit(10);

        hybatis
            .queryList(d.toSqlCommand())
            .forEach(System.out::println);
    }
}
