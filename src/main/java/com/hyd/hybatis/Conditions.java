package com.hyd.hybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Conditions {

    /**
     * query conditions
     */
    private final Map<String, Condition<?>> data = new HashMap<>();

    public Condition<Object> with(String column) {
        var c = new Condition<>();
        c.setColumn(column);
        this.data.put(column, c);
        return c;
    }

    public Conditions with(String column, Consumer<Condition<Object>> consumer) {
        consumer.accept(with(column));
        return this;
    }

    public List<Condition<?>> getConditions() {
        return new ArrayList<>(data.values());
    }
}
