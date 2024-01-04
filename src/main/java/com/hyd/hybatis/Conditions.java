package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlHelper;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Getter
public class Conditions implements Serializable, Cloneable {

    public static Conditions eq(String columnName, Object value) {
        return new Conditions().withColumn(columnName).eq(value);
    }

    public static Conditions ne(String columnName, Object value) {
        return new Conditions().withColumn(columnName).ne(value);
    }

    public static Conditions lt(String columnName, Object value) {
        return new Conditions().withColumn(columnName).lt(value);
    }

    public static Conditions lte(String columnName, Object value) {
        return new Conditions().withColumn(columnName).lte(value);
    }

    public static Conditions gt(String columnName, Object value) {
        return new Conditions().withColumn(columnName).gt(value);
    }

    public static Conditions gte(String columnName, Object value) {
        return new Conditions().withColumn(columnName).gte(value);
    }

    public static Conditions between(String columnName, Object o1, Object o2) {
        return new Conditions().withColumn(columnName).between(o1, o2);
    }

    public static Conditions in(String columnName, List<?> tt) {
        return new Conditions().withColumn(columnName).inList(tt);
    }

    public static Conditions in(String columnName, Object... tt) {
        return new Conditions().withColumn(columnName).in(tt);
    }

    public static Conditions nin(String columnName, List<?> tt) {
        return new Conditions().withColumn(columnName).ninList(tt);
    }

    public static Conditions nin(String columnName, Object... tt) {
        return new Conditions().withColumn(columnName).nin(tt);
    }

    public static Conditions beNull(String columnName) {
        return new Conditions().withColumn(columnName).beNull();
    }

    public static Conditions nonNull(String columnName) {
        return new Conditions().withColumn(columnName).nonNull();
    }

    ////////////////////////////////////////

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
        public Conditions nin(List<?> tt) {
            return ninList(tt);
        }

        public Conditions orderAsc(int order) {
            return Conditions.this.with(column, c -> c.setOrderAsc(order));
        }

        public Conditions orderDesc(int order) {
            return Conditions.this.with(column, c -> c.setOrderDesc(order));
        }

        @SafeVarargs
        public final <T> Conditions in(T... tt) {
            if (tt == null || tt.length == 0) {
                return Conditions.this.with(column, c -> {
                });
            } else if (tt[0] instanceof List) {
                return inList((List<?>) tt[0]);
            } else {
                return inList(Arrays.asList(tt));
            }
        }

        private Conditions inList(List<?> tt) {
            return Conditions.this.with(column, c -> c.in(tt));
        }

        @SafeVarargs
        public final <T> Conditions nin(T... tt) {
            if (tt == null || tt.length == 0) {
                return Conditions.this.with(column, c -> {
                });
            } else if (tt[0] instanceof List) {
                return ninList((List<?>) tt[0]);
            } else {
                return ninList(Arrays.asList(tt));
            }
        }

        private Conditions ninList(List<?> tt) {
            return Conditions.this.with(column, c -> c.nin(tt));
        }
    }

    private static final long serialVersionUID = 43210L;

    /**
     * query conditions
     * -- GETTER --
     *  Allow user to manipulate the conditions

     */
    private final Map<String, Condition<?>> query = new HashMap<>();

    private List<String> projection = Collections.emptyList();

    private int limit = -1;

    ////////////////////////////////// equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conditions that = (Conditions) o;
        return limit == that.limit && Objects.equals(query, that.query) && Objects.equals(projection, that.projection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, projection, limit);
    }

    //////////////////////////////////

    public Conditions projection(String... projection) {
        this.projection = List.of(projection);
        return this;
    }

    public Conditions projection(Collection<String> projection) {
        this.projection = new ArrayList<>(projection);
        return this;
    }

    public Conditions ensureProjection(Collection<String> projections) {
        if (!projection.isEmpty()) {
            for (String p : projections) {
                if (!projection.contains(p)) {
                    projection.add(p);
                }
            }
        }
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
        var index = new AtomicInteger(maxOrderIndex());
        for (String column : ascColumns) {
            var c = this.query.computeIfAbsent(column, __ -> new Condition<>());
            c.setColumn(column);
            c.setOrderAsc(index.incrementAndGet());
        }
        return this;
    }

    public Conditions orderDesc(String... ascColumns) {
        var index = new AtomicInteger(maxOrderIndex());
        for (String column : ascColumns) {
            var c = this.query.computeIfAbsent(column, __ -> new Condition<>());
            c.setColumn(column);
            c.setOrderDesc(index.incrementAndGet());
        }
        return this;
    }

    private int maxOrderIndex() {
        return this.query.values().stream()
            .filter(c -> c.getOrderAsc() != null || c.getOrderDesc() != null)
            .mapToInt(c -> c.getOrderAsc() == null ? c.getOrderDesc() : c.getOrderAsc())
            .max().orElse(0);
    }

    public Wrapper withColumn(String column) {
        return new Wrapper(column);
    }

    public Set<String> conditionKeySet() {
        return query.keySet();
    }

    public List<Condition<?>> conditionsList() {
        return new ArrayList<>(query.values());
    }

    public Condition<?> getCondition(String column) {
        return query.getOrDefault(column, Condition.EMPTY);
    }

    public boolean containsColumn(String column) {
        return query.containsKey(column);
    }

    public Sql.Select toSelect(String tableName) {
        return SqlHelper.buildSelectFromConditions(this, tableName);
    }

    /**
     * Create a new Conditions object with specified keys
     */
    public Conditions pick(String... keys) {
        var conditions = new Conditions();
        for (String key : keys) {
            var condition = query.get(key);
            if (condition != null) {
                conditions.query.put(key, condition);
            }
        }
        conditions.projection = new ArrayList<>(this.projection);
        conditions.limit = this.limit;
        return conditions;
    }

    @Override
    public Conditions clone() {
        try {
            Conditions clone = (Conditions) super.clone();
            clone.query.putAll(query);
            clone.projection = new ArrayList<>(this.projection);
            clone.limit = this.limit;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
