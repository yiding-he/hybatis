package com.hyd.hybatis.query;

import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.query.match.Equal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CompositeMatchNotTest {

    @Test
    public void testNotOperator() {
        // 创建一个等于匹配条件
        Equal<Department> equalMatch = Match.equal(Department::getDeptNo, "d001");

        // 使用 NOT 包装该条件
        var notMatch = Match.NOT(equalMatch);

        // 验证匹配条件列表包含一个元素
        assertEquals(1, notMatch.getMatches().size());
        assertSame(equalMatch, notMatch.getMatches().get(0));
    }
}
