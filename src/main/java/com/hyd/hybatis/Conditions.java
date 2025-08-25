package com.hyd.hybatis;

import com.hyd.hybatis.sql.Sql;
import com.hyd.hybatis.sql.SqlHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    /// /////////////////////////////////////

    @SuppressWarnings({"unused"})
    public class Wrapper {

        private final String column;

        public Wrapper(String column) {
            this.column = column;
        }

        public Conditions startWith(String s) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.StartsWith, s));
        }

        public Conditions endsWith(String s) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.EndsWith, s));
        }

        public Conditions contains(String s) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Contains, s));
        }

        public Conditions eq(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Eq, o));
        }

        public Conditions ne(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Ne, o));
        }

        public Conditions beNull() {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Null));
        }

        public Conditions nonNull() {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.NonNull));
        }

        public Conditions lt(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Lt, o));
        }

        public Conditions lte(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Lte, o));
        }

        public Conditions gt(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Gt, o));
        }

        public Conditions gte(Object o) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Gte, o));
        }

        public Conditions between(Object o1, Object o2) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Between, o1, o2));
        }

        public Conditions in(List<?> tt) {
            return inList(tt);
        }

        public Conditions nin(List<?> tt) {
            return ninList(tt);
        }

        public Conditions orderAsc(int order) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.OrderAsc, order));
        }

        public Conditions orderDesc(int order) {
            return Conditions.this.with(column, c -> c.update(ConditionOperator.OrderDesc, order));
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
            return Conditions.this.with(column, c -> c.update(ConditionOperator.In, tt));
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
            return Conditions.this.with(column, c -> c.update(ConditionOperator.Nin, tt));
        }
    }

    private static final long serialVersionUID = 43210L;

    /**
     * query conditions
     */
    private final List<Condition> query = new ArrayList<>();

    public void setQuery(List<Condition> query) {
        this.query.clear();
        this.query.addAll(query);
    }

    @Setter
    private List<String> projection = Collections.emptyList();

    @Setter
    private int offset = 0;

    @Setter
    private int limit = -1;

    //-------------------------- equals and hashcode --------------------------

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

    //-------------------------- operators --------------------------

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

    public Condition with(String column) {
        Condition c = new Condition(column);
        this.query.add(c);
        return c;
    }

    public Conditions with(String column, Consumer<Condition> consumer) {
        consumer.accept(with(column));
        return this;
    }

    public Conditions orderAsc(String... ascColumns) {
        var index = new AtomicInteger(maxOrderIndex());
        for (String column : ascColumns) {
            this.getOrCreateCondition(column)
                .update(ConditionOperator.OrderAsc, index.incrementAndGet());
        }
        return this;
    }

    public Conditions orderDesc(String... ascColumns) {
        var index = new AtomicInteger(maxOrderIndex());
        for (String column : ascColumns) {
            this.getOrCreateCondition(column)
                .update(ConditionOperator.OrderDesc, index.incrementAndGet());
        }
        return this;
    }

    private int maxOrderIndex() {
        return this.query.stream()
            .filter(c -> c.getOperator() == ConditionOperator.OrderAsc || c.getOperator() == ConditionOperator.OrderDesc)
            .mapToInt(c -> Integer.parseInt(String.valueOf(c.getValues().get(0))))
            .max().orElse(0);
    }

    public Wrapper withColumn(String column) {
        return new Wrapper(column);
    }

    public Set<String> conditionKeySet() {
        return this.query.stream().map(Condition::getColumn).collect(Collectors.toSet());
    }

    public List<Condition> conditionsList() {
        return new ArrayList<>(query);
    }

    public Condition getCondition(String column, ConditionOperator operator) {
        return this.query.stream()
            .filter(c -> c.getColumn().equals(column) && c.getOperator() == operator)
            .findFirst().orElse(null);
    }

    public Condition getOrCreateCondition(String column) {
        var condition = this.query.stream()
            .filter(c -> c.getColumn().equals(column))
            .findFirst().orElse(null);

        if (condition == null) {
            condition = new Condition(column);
            this.query.add(condition);
        }

        return condition;
    }

    public boolean containsColumn(String column) {
        return query.stream().anyMatch(c -> c.getColumn().equals(column));
    }

    public Sql.Select toSelect(String tableName) {
        return SqlHelper.buildSelectFromConditions(this, tableName);
    }

    /**
     * Create a new Conditions object with specified keys
     */
    public Conditions pick(String... keys) {
        var conditions = new Conditions();
        var keySet = Set.of(keys);

        this.query.stream()
            .filter(c -> keySet.contains(c.getColumn()))
            .forEach(conditions.query::add);

        conditions.projection = new ArrayList<>(this.projection);
        conditions.limit = this.limit;
        conditions.offset = this.offset;
        return conditions;
    }

    @Override
    public Conditions clone() {
        try {
            Conditions clone = (Conditions) super.clone();
            clone.query.addAll(query);
            clone.projection = new ArrayList<>(this.projection);
            clone.limit = this.limit;
            clone.offset = this.offset;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
