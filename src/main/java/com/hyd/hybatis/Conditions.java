package com.hyd.hybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Conditions extends HashMap<String, Condition<?>> {

    public Condition<Object> with(String column) {
        var c = new Condition<>();
        c.setColumn(column);
        put(column, c);
        return c;
    }

    public List<Condition<?>> getConditions() {
        return new ArrayList<>(values());
    }
}
