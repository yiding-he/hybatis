package com.hyd.hybatis.tests;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.mappers.DepartmentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    public void testSelectList() {
        var departments = departmentMapper.selectList(new Conditions());
        departments.forEach(System.out::println);
    }

    @Test
    public void testUpdate() {
        var update = new Department();
        update.setDeptName("Customer Service 客户服务");
        departmentMapper.update(
            new Conditions().withColumn("dept_no").eq("d009"), update);
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
        int count = departmentMapper.delete(new Conditions().withColumn("dept_no").eq("d010"));
        System.out.println("count = " + count);
    }
}
