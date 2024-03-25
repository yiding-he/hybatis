package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.query.Match;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Aggregate.count;

class JoinTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void test1() throws Exception {
        var e = new TableOrView("EMPLOYEES").as("e");
        var de = new TableOrView("DEPT_EMP").as("de");

        var join = new Join(
            e, de,
            Join.JoinType.Left,
            Match.equal(e.col("EMP_NO"), de.col("EMP_NO"))
        ).columns(
            e.col("GENDER"), de.col("DEPT_NO")
        ).aggregates(
            count(e.col("EMP_NO")).as("EMP_COUNT")
        ).groupBy(
            e.col("GENDER"), de.col("DEPT_NO")
        );

        var sqlCommand = join.toSqlCommand();
        System.out.println("sqlCommand.getStatement() = " + sqlCommand.getStatement());
        System.out.println("sqlCommand.getParams() = " + sqlCommand.getParams());

        hybatis.queryList(sqlCommand).forEach(System.out::println);
    }
}
