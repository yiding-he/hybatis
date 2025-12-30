package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.entity.DeptEmp;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.query.query.Table;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.hyd.hybatis.query.Filter.*;

public class ExistsTest extends HybatisSpringBootTestApplicationTest {

    @Test
    void testExists() throws SQLException {
        var d = Table.of(Department.class);
        var de = Table.of(DeptEmp.class);
        var e = Table.of(Employee.class);

        // This is a bad sample.
        // It causes StackOverflowError.
        var query = d.filter(
            exists(d
                .join(join -> join.with(de).match(
                    de.col(DeptEmp::getDeptNo), d.col(Department::getDeptNo)
                ))
                .join(join -> join.with(e).match(
                    de.col(DeptEmp::getEmpNo), e.col(Employee::getEmpNo)
                ))
                .filter(equal(e.col(Employee::getEmpNo), 10010))
            )
        );

        hybatis.queryList(query.toSqlCommand()).forEach(System.out::println);
    }

}
