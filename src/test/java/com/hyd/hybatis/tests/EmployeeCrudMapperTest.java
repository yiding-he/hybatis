package com.hyd.hybatis.tests;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.mappers.EmployeeCrudMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeCrudMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private EmployeeCrudMapper employeeCrudMapper;

    @Test
    void testSelectList() {
        var employees = employeeCrudMapper.selectList(new Conditions()
            .withColumn("first_name").startWith("B")
            .orderDesc("last_name", "emp_no")
            .limit(10)
        );
        employees.forEach(System.out::println);
    }

    @Test
    void testSelectPage() {
        var page = employeeCrudMapper.selectPage(new Conditions()
                .withColumn("first_name").startWith("B")
                .orderDesc("last_name", "emp_no"),
            2, 10);

        System.out.println("page.getTotal() = " + page.getTotal());
        System.out.println("page.getPages() = " + page.getPages());
        System.out.println("page.getPageNum() = " + page.getPageNum());
        System.out.println("page.getPageSize() = " + page.getPageSize());
        page.getList().forEach(System.out::println);
    }
}
