package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Column.*;

public class CaseWhenTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testCaseWhen() throws Exception {
        TableOrView emp = new TableOrView("EMPLOYEES").as("e");
        var query = emp.columns(
            sum(cases().when(emp.col("first_name").eq("Abdelaziz"), lit(1))).as("count_abdelaziz"),
            sum(cases().when(emp.col("first_name").eq("Georgi"), lit(1))).as("count_georgi")
        ).groupBy(
            emp.col("gender")
        );

        hybatis.queryList(query.toSqlCommand())
            .forEach(System.out::println);
    }
}
