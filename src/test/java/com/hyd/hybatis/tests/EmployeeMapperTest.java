package com.hyd.hybatis.tests;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.EmployeeQuery;
import com.hyd.hybatis.mappers.EmployeeMapper;
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
        query.setEmpNo(new Condition<Integer>().lt(10100));

        var employees = employeeMapper.selectByQuery(query);
        assertFalse(employees.isEmpty());
        assertEquals(99, employees.size());

        var employee = employees.get(0);
        assertNotNull(employee.getEmpNo());
        assertNotNull(employee.getFirstName());
        assertNotNull(employee.getLastName());
    }

    @Test
    public void testSelectByConditions() {
        var rows = employeeMapper.selectByConditions(new Conditions()
            .withColumn("first_name").startWith("B")
            .orderDesc("last_name", "emp_no")
            .limit(10)
        );
        rows.forEach(System.out::println);
    }
}
