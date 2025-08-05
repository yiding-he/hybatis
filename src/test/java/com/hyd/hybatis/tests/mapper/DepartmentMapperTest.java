package com.hyd.hybatis.tests.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.mappers.DepartmentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    public void testSelectAll() throws Exception {
        departmentMapper.selectAll().forEach(System.out::println);
    }

    @Test
    public void testSelectList() {
        var departments = departmentMapper.selectList(
            new Conditions()
                .withColumn("dept_name").nin("Finance", "Development")
                .withColumn("dept_no").gt("d001")
                .orderAsc("dept_name")
                .limit(5)
        );
        assertFalse(departments.isEmpty());
        departments.forEach(System.out::println);
    }

    @Test
    public void testInsertIgnore() throws Exception {
        var department = new Department();
        department.setDeptNo("d010");
        department.setDeptName("AAAAAAAAAAAA");
        assertEquals(0, departmentMapper.insertIgnore(department));

        department = departmentMapper.findById("d010");
        assertEquals("新的部门", department.getDeptName());
    }

    @Test
    void testSelectOne() {
        var department = departmentMapper.selectOne(
            new Conditions().withColumn("dept_no").eq("d005"));
        assertNotNull(department);
        assertEquals("d005", department.getDeptNo());
        assertEquals("Development", department.getDeptName());

        department = departmentMapper.selectOne(
            new Conditions().withColumn("dept_no").eq("d999"));
        assertNull(department);
    }

    @Test
    public void testUpdate() {
        var conditions = new Conditions()
            .withColumn("dept_no").eq("d009")
            .withColumn("dept_name").startWith("Customer");

        var update = new Department();
        update.setDeptName("Customer Service 客户服务2");

        var count = departmentMapper.update(conditions, update);
        assertTrue(count > 0);

        System.out.println(departmentMapper.selectOne(conditions));
    }

    @Test
    public void testInsert() {
        var department = new Department();
        department.setDeptNo("d010");
        department.setDeptName("新的部门");
        departmentMapper.insert(department);
        departmentMapper.selectAll().forEach(System.out::println);
    }

    @Test
    public void testDelete() throws Exception {
        int count = departmentMapper.delete(
            new Conditions().withColumn("dept_no").eq("d010")
        );
        System.out.println("count = " + count);
    }
}
