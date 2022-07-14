package com.hyd.hybatis.reflection;

import com.hyd.hybatis.Conditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionsTest {

    @Test
    public void testIsConditionsQueryType() throws Exception {
        assertTrue(Reflections.isPojoClassQueryable(Conditions.class));
    }
}
