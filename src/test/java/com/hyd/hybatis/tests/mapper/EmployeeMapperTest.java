package com.hyd.hybatis.tests.mapper;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.ConditionOperator;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.mappers.EmployeeMapper;
import com.hyd.hybatis.query.EmployeeQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    public void testInitialized() throws Exception {
        assertNotNull(employeeMapper);
    }

    @Test
    public void testSelectByQuery() throws Exception {
        var query = new EmployeeQuery();
        query.setEmpNo(new Condition().update(ConditionOperator.Lt, 10100));

        var employees = employeeMapper.selectByQuery(query);
        assertFalse(employees.isEmpty());
        assertEquals(99, employees.size());

        var employee = employees.get(0);
        assertNotNull(employee.getEmpNo());
        assertNotNull(employee.getFirstName());
        assertNotNull(employee.getLastName());
    }

    @Test
    public void testSelectLimit1() {
        var employees = employeeMapper.selectByConditions(new Conditions()
           .withColumn("first_name").startWith("B")
           .orderDesc("last_name", "emp_no")
           .limit(1)
        );
        assertFalse(employees.isEmpty());
        employees.forEach(System.out::println);
    }

    @Test
    public void testSelectByConditions() {
        var rows = employeeMapper.selectRowsByConditions(new Conditions()
            .withColumn("first_name").startWith("B")
            .orderDesc("last_name", "emp_no")
            .limit(10)
        );
        rows.forEach(System.out::println);
    }

    @Test
    public void testCountByConditions() throws Exception {
        var count = employeeMapper.countByConditions(new Conditions()
            .withColumn("first_name").startWith("B"));
        System.out.println("count = " + count);
    }
}
