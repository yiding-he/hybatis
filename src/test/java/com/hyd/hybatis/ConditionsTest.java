package com.hyd.hybatis;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionsTest {

    @Test
    public void testNin() throws Exception {
        var conditions = new Conditions().withColumn("a").nin(1, 2, 3);
        assertEquals(1, conditions.getQuery().size());
        assertEquals("a", conditions.getCondition("a", ConditionOperator.Nin).getColumn());

        var command = conditions.toSelect("t").toCommand();
        assertEquals(3, command.getParams().size());
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }

    @Test
    public void testFastjson2Serialization() {
        var conditions = new Conditions().withColumn("a").nin(1, 2, 3);
        var json = JSON.toJSONString(conditions);
        System.out.println("json = " + json);
        var deserialized = JSON.parseObject(json, Conditions.class);
        assertEquals(conditions, deserialized);

        // 简化版本
        var json2 = "{\"query\":{\"a\":{\"nin\":[1,2,3]}}}";
        var deserialized2 = JSON.parseObject(json2, Conditions.class);
        assertEquals(conditions, deserialized2);
    }

    @Test
    public void testJacksonSerialization() throws Exception {
        var objectMapper = new ObjectMapper();
        var conditions = new Conditions().withColumn("a").nin(1, 2, 3);
        var json = objectMapper.setSerializationInclusion(NON_NULL).writeValueAsString(conditions);
        System.out.println("json = " + json);

        var deserialized = objectMapper.readValue(json, Conditions.class);
        assertEquals(conditions, deserialized);

        // 简化版本
        var json2 = "{\"query\":{\"a\":{\"nin\":[1,2,3]}}}";
        var deserialized2 = objectMapper.readValue(json2, Conditions.class);
        assertEquals(conditions, deserialized2);
    }
}
