package com.hyd.hybatis.query;

import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.query.filter.Equal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CompositeFilterNotTest {

    @Test
    public void testNotOperator() {
        // 创建一个等于匹配条件
        Equal<Department> equalMatch = Filter.equal(Department::getDeptNo, "d001");

        // 使用 NOT 包装该条件
        var notMatch = Filter.NOT(equalMatch);

        // 验证匹配条件列表包含一个元素
        assertEquals(1, notMatch.getFilters().size());
        assertSame(equalMatch, notMatch.getFilters().get(0));
    }
}
