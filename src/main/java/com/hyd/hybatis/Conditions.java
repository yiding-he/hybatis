package com.hyd.hybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Conditions implements Serializable {

    @SuppressWarnings({"Convert2MethodRef", "unused"})
    public class Wrapper {

        private final String column;

        public Wrapper(String column) {
            this.column = column;
        }

        public Conditions startWith(String s) {
            return Conditions.this.with(column, c -> c.startsWith(s));
        }

        public Conditions endsWith(String s) {
            return Conditions.this.with(column, c -> c.endsWith(s));
        }

        public Conditions contains(String s) {
            return Conditions.this.with(column, c -> c.contains(s));
        }

        public Conditions eq(Object o) {
            return Conditions.this.with(column, c -> c.eq(o));
        }

        public Conditions ne(Object o) {
            return Conditions.this.with(column, c -> c.ne(o));
        }

        public Conditions beNull() {
            return Conditions.this.with(column, c -> c.beNull());
        }

        public Conditions nonNull() {
            return Conditions.this.with(column, c -> c.nonNull());
        }

        public Conditions lt(Object o) {
            return Conditions.this.with(column, c -> c.lt(o));
        }

        public Conditions lte(Object o) {
            return Conditions.this.with(column, c -> c.lte(o));
        }

        public Conditions gt(Object o) {
            return Conditions.this.with(column, c -> c.gt(o));
        }

        public Conditions gte(Object o) {
            return Conditions.this.with(column, c -> c.gte(o));
        }

        public Conditions between(Object o1, Object o2) {
            return Conditions.this.with(column, c -> c.between(o1, o2));
        }

        public Conditions in(List<?> tt) {
            return Conditions.this.with(column, c -> c.in(tt));
        }

        @SafeVarargs
        public final <T> Conditions in(T... tt) {
            return Conditions.this.with(column, c -> c.in(tt));
        }
    }

    private static final long serialVersionUID = 43210L;

    /**
     * query conditions
     */
    private final Map<String, Condition<?>> data = new HashMap<>();

    private int limit = -1;

    public int getLimit() {
        return limit;
    }

    public Conditions limit(int limit) {
        this.limit = limit;
        return this;
    }

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

    public Conditions orderAsc(String... ascColumns) {
        var index = new AtomicInteger(getMaxOrderIndex());
        for (String column : ascColumns) {
            var c = new Condition<>();
            c.setColumn(column);
            c.setOrderAsc(index.incrementAndGet());
            this.data.put(column, c);
        }
        return this;
    }

    public Conditions orderDesc(String... ascColumns) {
        var index = new AtomicInteger(getMaxOrderIndex());
        for (String column : ascColumns) {
            var c = new Condition<>();
            c.setColumn(column);
            c.setOrderDesc(index.incrementAndGet());
            this.data.put(column, c);
        }
        return this;
    }

    private int getMaxOrderIndex() {
        return this.data.values().stream()
            .filter(c -> c.getOrderAsc() != null || c.getOrderDesc() != null)
            .mapToInt(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc())
            .max().orElse(0);
    }

    public Wrapper withColumn(String column) {
        return new Wrapper(column);
    }

    public List<Condition<?>> getConditions() {
        return new ArrayList<>(data.values());
    }
}
