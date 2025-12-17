package com.hyd.hybatis.query;

import com.hyd.hybatis.entity.Department;
import org.junit.jupiter.api.Test;

public class ColumnTest {

    @Test
    public void testPropColumn() {
        var prop = Column.prop(Department::getDeptName);
    }
}
