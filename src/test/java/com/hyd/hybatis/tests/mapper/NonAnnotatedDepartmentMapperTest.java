package com.hyd.hybatis.tests.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.mappers.NonAnnotatedDepartmentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NonAnnotatedDepartmentMapperTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private NonAnnotatedDepartmentMapper nonAnnotatedDepartmentMapper;

    @Test
    public void testListAll() throws Exception {
        var departments = nonAnnotatedDepartmentMapper.selectList(new Conditions());
        departments.forEach(System.out::println);
    }
}
