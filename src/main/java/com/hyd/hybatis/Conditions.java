package com.hyd.hybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Conditions extends HashMap<String, Condition<?>> {

    public Condition<Object> with(String column) {
        var c = new Condition<>();
        c.setColumn(column);
        put(column, c);
        return c;
    }

    public Conditions with(String column, Consumer<Condition<Object>> consumer) {
        consumer.accept(with(column));
        return this;
    }

    public List<Condition<?>> getConditions() {
        return new ArrayList<>(values());
    }
}
