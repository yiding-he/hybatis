package com.hyd.hybatis.utils;

import com.hyd.hybatis.mappers.EmployeeRowMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MapperUtilTest {

    @Test
    public void testPrimaryKeyNames() throws Exception {
        var primaryKeyNames = MapperUtil.primaryKeyNames(EmployeeRowMapper.class);
        System.out.println("primaryKeyNames = " + Arrays.toString(primaryKeyNames));
    }
}