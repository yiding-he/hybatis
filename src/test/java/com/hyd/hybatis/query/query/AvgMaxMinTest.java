package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Column.*;

public class AvgMaxMinTest extends HybatisSpringBootTestApplicationTest {

    // FIXME 这个测试不能通过
    @Test
    public void testAvgMaxMin() throws Exception {
        var emp = new TableOrView("EMPLOYEES").as("E");
        var sal = new TableOrView("SALARIES").as("S");

        var sv = sal
            .columns(avg(sal.col("SALARY")).as("avg_salary"))
            .groupBy(sal.col("EMP_NO"))
            .as("SV");

        var join = sv.join(
            j -> j.with(emp).using("EMP_NO")
        ).columns(
            emp.col("FIRST_NAME"),
            sal.col("avg_salary")
        ).as("T");

        var query = join
            .columns(
                max(join.col("avg_salary")).as("max_salary"),
                min(join.col("avg_salary")).as("min_salary"),
                avg(join.col("avg_salary")).as("avg_salary")
            )
            .groupBy(join.col("FIRST_NAME"));

        hybatis.queryList(query.toSqlCommand()).forEach(System.out::println);
    }
}
