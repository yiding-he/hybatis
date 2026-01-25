package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.entity.DeptEmp;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.query.QueryContextTracker;
import com.hyd.hybatis.query.query.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.hyd.hybatis.query.Filter.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExistsTest extends HybatisSpringBootTestApplicationTest {

    @AfterEach
    void cleanup() {
        QueryContextTracker.clear();
    }

    @Test
    void testCircularReferenceDetection() {
        var d = Table.of(Department.class);
        var de = Table.of(DeptEmp.class);
        var e = Table.of(Employee.class);

        // This is a bad sample.
        // It used to cause StackOverflowError, now should be detected.
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

        // 现在应该检测到循环引用并抛出异常，而不是 StackOverflowError
        IllegalStateException exception = assertThrows(IllegalStateException.class, query::toSqlCommand);

        // 验证异常消息包含有用的提示
        assertTrue(exception.getMessage().contains("circular reference"));
        assertTrue(exception.getMessage().contains("EXISTS"));
    }

    @Test
    void testCorrectExistsUsage() throws SQLException {
        var d = Table.of(Department.class);
        var dSub = Table.of(Department.class);  // 使用不同的对象
        var de = Table.of(DeptEmp.class);
        var e = Table.of(Employee.class);

        // 正确的使用方式
        var query = d.filter(
            exists(dSub
                .join(join -> join.with(de).match(
                    de.col(DeptEmp::getDeptNo), dSub.col(Department::getDeptNo)
                ))
                .join(join -> join.with(e).match(
                    de.col(DeptEmp::getEmpNo), e.col(Employee::getEmpNo)
                ))
                .filter(equal(e.col(Employee::getEmpNo), 10010))
            )
        );

        // 应该正常执行
        assertDoesNotThrow(() -> {
            hybatis.queryList(query.toSqlCommand()).forEach(System.out::println);
        });
    }

}
