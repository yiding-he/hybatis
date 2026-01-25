package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.entity.DeptEmp;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.query.QueryContextTracker;
import com.hyd.hybatis.query.query.Table;
import com.hyd.hybatis.query.query.Wrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.hyd.hybatis.query.Filter.equal;
import static com.hyd.hybatis.query.Filter.exists;
import static org.junit.jupiter.api.Assertions.*;

public class CircularReferenceDetectionTest extends HybatisSpringBootTestApplicationTest {

    @AfterEach
    void cleanup() {
        QueryContextTracker.clear();
    }

    @Test
    void testDetectCircularReferenceInExists() {
        var d = Table.of(Department.class);
        var de = Table.of(DeptEmp.class);
        var e = Table.of(Employee.class);

        // 构建会导致循环引用的查询
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

        // 应该检测到循环引用并抛出异常
        IllegalStateException exception = assertThrows(IllegalStateException.class, query::toSqlCommand);

        // 验证异常消息
        assertTrue(exception.getMessage().contains("circular reference"));
        assertTrue(exception.getMessage().contains("EXISTS"));
        assertTrue(exception.getMessage().contains("same query object"));
    }

    @Test
    void testNormalExistsWorks() {
        var d = Table.of(Department.class);
        var dSub = Table.of(Department.class);  // 使用不同的对象
        var de = Table.of(DeptEmp.class);
        var e = Table.of(Employee.class);

        // 构建正常的查询（使用不同的查询对象）
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

        // 应该正常生成 SQL 而不抛出异常
        assertDoesNotThrow(() -> {
            var sql = query.toSqlCommand();
            assertNotNull(sql);
            assertTrue(sql.getStatement().contains("EXISTS"));
        });
    }

    @Test
    void testNestedExistsWithoutCircularReference() {
        var d1 = Table.of(Department.class);
        var d2 = Table.of(Department.class);
        var e = Table.of(Employee.class);

        // 嵌套的 EXISTS，但不是循环引用
        var query = d1.filter(
            exists(d2.filter(
                exists(e.filter(equal(e.col(Employee::getEmpNo), 10010)))
            ))
        );

        // 应该正常工作
        assertDoesNotThrow(() -> {
            var sql = query.toSqlCommand();
            assertNotNull(sql);
            // 应该包含两个 EXISTS
            var statement = sql.getStatement();
            int existsCount = countOccurrences(statement, "EXISTS");
            assertEquals(2, existsCount);
        });
    }

    private int countOccurrences(String str, String substr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substr, index)) != -1) {
            count++;
            index += substr.length();
        }
        return count;
    }

    @Test
    void testDetectCircularReferenceInWrap() {
        var d = Table.of(Department.class);

        // 创建自引用的 Wrap
        var wrap = new Wrap(d);
        d.filter(exists(wrap));  // 这样会形成循环：d -> wrap -> d

        // 应该检测到循环引用并抛出异常
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            d.toSqlCommand();
        });

        assertTrue(exception.getMessage().contains("circular reference"));
    }
}
