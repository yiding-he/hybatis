package com.hyd.hybatis.utils;

import com.hyd.hybatis.mappers.EmployeeRowMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperUtilTest {

    @Test
    public void testPrimaryKeyNames() throws Exception {
        var primaryKeyNames = MapperUtil.getPrimaryKeys(EmployeeRowMapper.class);
        assertEquals(1, primaryKeyNames.length);
        assertEquals("EMP_NO", primaryKeyNames[0]);
    }
}
