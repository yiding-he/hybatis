package com.hyd.hybatis;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    public void testCondition() throws Exception {
        Condition condition = new Condition("name", "Eq", "张三", "李四");
        System.out.println("condition  = " + condition);

        String jsonString = JSON.toJSONString(condition);
        System.out.println("jsonString = " + jsonString);

        Condition condition2 = JSON.parseObject(jsonString, Condition.class);
        System.out.println("condition2 = " + condition2);
    }
}
