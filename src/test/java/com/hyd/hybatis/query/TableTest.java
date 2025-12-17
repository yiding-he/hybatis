package com.hyd.hybatis.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.query.query.Table;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.hyd.hybatis.query.Column.from;
import static com.hyd.hybatis.query.Match.between;
import static com.hyd.hybatis.query.Match.equal;

public class TableTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testCreateTableOrView() throws SQLException {
        var table = Table.of(Department.class);
        var query = table
            .matches(
                equal(from(table, Department::getDeptName), "Sales"),
                between(from(table, Department::getDeptNo), "d001", "d999")
            )
            .limit(10);
        hybatis.queryList(query.toSqlCommand()).forEach(System.out::println);
    }
}
