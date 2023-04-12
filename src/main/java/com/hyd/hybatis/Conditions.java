package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlHelper;

import java.io.Serializable;
import java.util.*;
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
            return inList(tt);
        }

        @SafeVarargs
        public final <T> Conditions in(T... tt) {
            if (tt == null || tt.length == 0) {
                return Conditions.this.with(column, c -> {});
            } else if (tt[0] instanceof List) {
                return inList((List<?>) tt[0]);
            } else {
                return inList(Arrays.asList(tt));
            }
        }

        private Conditions inList(List<?> tt) {
            return Conditions.this.with(column, c -> c.in(tt));
        }
    }

    private static final long serialVersionUID = 43210L;

    /**
     * query conditions
     */
    private final Map<String, Condition<?>> query = new HashMap<>();

    private List<String> projection = Collections.emptyList();

    private int limit = -1;

    public int getLimit() {
        return limit;
    }

    public List<String> getProjection() {
        return projection;
    }

    public Conditions projection(String... projection) {
        this.projection = List.of(projection);
        return this;
    }

    public Conditions projection(Collection<String> projection) {
        this.projection = new ArrayList<>(projection);
        return this;
    }

    public Conditions limit(int limit) {
        this.limit = limit;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Condition<Object> with(String column) {
        Condition<Object> c;
        if (this.query.containsKey(column)) {
            c = (Condition<Object>) this.query.get(column);
        } else {
            c = new Condition<>();
            c.setColumn(column);
            this.query.put(column, c);
        }
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
            this.query.put(column, c);
        }
        return this;
    }

    public Conditions orderDesc(String... ascColumns) {
        var index = new AtomicInteger(getMaxOrderIndex());
        for (String column : ascColumns) {
            var c = new Condition<>();
            c.setColumn(column);
            c.setOrderDesc(index.incrementAndGet());
            this.query.put(column, c);
        }
        return this;
    }

    private int getMaxOrderIndex() {
        return this.query.values().stream()
            .filter(c -> c.getOrderAsc() != null || c.getOrderDesc() != null)
            .mapToInt(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc())
            .max().orElse(0);
    }

    public Wrapper withColumn(String column) {
        return new Wrapper(column);
    }

    public List<Condition<?>> getConditions() {
        return new ArrayList<>(query.values());
    }

    public Sql.Select toSelect(String tableName) {
        return SqlHelper.buildSelectFromConditions(this, tableName);
    }
}
