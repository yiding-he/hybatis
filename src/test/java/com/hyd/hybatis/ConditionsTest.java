package com.hyd.hybatis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionsTest {

    @Test
    public void testNin() throws Exception {
        var conditions = new Conditions().withColumn("a").nin(1, 2, 3);
        assertEquals(1, conditions.getQuery().size());
        assertEquals("a", conditions.getQuery().keySet().iterator().next());

        Condition<?> condition = conditions.getConditions().get(0);
        assertNotNull(condition.getNin());
        assertFalse(condition.getNin().isEmpty());

        var command = conditions.toSelect("t").toCommand();
        assertEquals(3, command.getParams().size());
        System.out.println(command.getStatement());
        System.out.println(command.getParams());
    }
}