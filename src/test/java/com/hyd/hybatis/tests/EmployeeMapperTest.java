package com.hyd.hybatis.tests;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.Page;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.entity.EmployeeQuery;
import com.hyd.hybatis.mappers.EmployeeMapper;
import com.hyd.hybatis.page.Pagination;
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
    public void testSelectPageByConditions() throws Exception {
        var conditions = new Conditions().with("emp_no", c -> c.lt(10100));
        var page = new Page<>(employeeMapper.selectPageByConditions(conditions));
        assertFalse(page.getList().isEmpty());
        assertEquals(99, page.getTotalRows());

        Pagination.setup(10, 3);
        page = new Page<>(employeeMapper.selectPageByConditions(conditions));
        assertFalse(page.getList().isEmpty());
        assertEquals(10, page.getList().size());
    }
}
