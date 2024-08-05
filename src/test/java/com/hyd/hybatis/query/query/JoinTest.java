package com.hyd.hybatis.query.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.query.query.Join.JoinType;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Column.count;

class JoinTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void test1() throws Exception {
        var e = new TableOrView("EMPLOYEES").as("e");
        var de = new TableOrView("DEPT_EMP").as("de");

        var join = e.join(
            _join -> _join.with(de).type(JoinType.Left).using("EMP_NO")
        ).columns(
            count(e.col("EMP_NO")).as("EMP_COUNT")
        ).groupBy(
            e.col("GENDER"),
            de.col("DEPT_NO")
        );

        var sqlCommand = join.toSqlCommand();
        System.out.println("sqlCommand.getStatement() = " + sqlCommand.getStatement());
        System.out.println("sqlCommand.getParams() = " + sqlCommand.getParams());

        hybatis.queryList(sqlCommand).forEach(System.out::println);
    }
}
