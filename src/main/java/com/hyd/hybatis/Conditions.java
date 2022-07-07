package com.hyd.hybatis;

import java.util.ArrayList;
import java.util.List;

public class Conditions {

    private final List<Condition<?>> conditions = new ArrayList<>();

    public Condition<Object> with(String column) {
        var c = new Condition<>();
        c.setColumn(column);
        conditions.add(c);
        return c;
    }

    public List<Condition<?>> getConditions() {
        return conditions;
    }
}
