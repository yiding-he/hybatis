package com.hyd.hybatis.tests.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.mappers.EmployeeRowMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmployeeRowMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private EmployeeRowMapper employeeRowMapper;

    @Test
    public void testQueryEmployees() throws Exception {
        var rows = employeeRowMapper.selectList(new Conditions().limit(10));
        rows.forEach(System.out::println);
    }

    @Test
    public void testSelectOne() throws Exception {
        var row = employeeRowMapper.selectOne(new Conditions()
            .withColumn("first_name").eq("Georgi")
            .withColumn("last_name").eq("Facello")
        );
        assertNotNull(row);
        assertEquals("M", row.getString("GENDER"));
    }
}
