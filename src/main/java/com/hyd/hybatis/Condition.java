package com.hyd.hybatis;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class Condition<T> implements Serializable {

    private static final long serialVersionUID = 43211L;

    public static final Condition<?> EMPTY = new Condition<>() {

        private static final long serialVersionUID = -1826616019465243670L;

        @Override
        public void setColumn(String column) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setStartsWith(String startsWith) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setEndsWith(String endsWith) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setContains(String contains) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setEq(Object eq) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setNe(Object ne) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setNull(Boolean Null) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setLt(Object lt) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setLte(Object lte) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setGt(Object gt) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setGte(Object gte) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setIn(List<Object> in) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setOrderAsc(Integer orderAsc) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setOrderDesc(Integer orderDesc) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public void setBetween(List<Object> values) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> startsWith(String s) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> endsWith(String s) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> contains(String s) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> eq(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> ne(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> beNull() {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> nonNull() {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> lt(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> lte(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> gt(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> gte(Object o) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> between(Object t1, Object t2) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }

        @Override
        public Condition<Object> in(List<Object> tt) {
            throw new UnsupportedOperationException("Unmodifiable Empty Condition");
        }
    };

    public static <T> Condition<T> of(String column) {
        Condition<T> c = new Condition<>();
        c.column = column;
        return c;
    }

    private String column;

    private String startsWith;  // like '...%'

    private String endsWith;    // like '%...'

    private String contains;    // like '%...%'

    private T eq;               // =

    private T ne;               // !=

    private Boolean Null;       // is (not) null

    private T lt;               // <

    private T lte;              // <=

    private T gt;               // >

    private T gte;              // >=

    private List<T> in;         // in

    private List<T> nin;        // not in

    private Integer orderAsc;   // order by ... asc

    private Integer orderDesc;  // order by ... desc

    public void setBetween(List<T> values) {
        between(values.get(0), values.get(1));
    }

    public Condition<T> startsWith(String s) {
        this.startsWith = s;
        return this;
    }

    public Condition<T> endsWith(String s) {
        this.endsWith = s;
        return this;
    }

    public Condition<T> contains(String s) {
        this.contains = s;
        return this;
    }

    public Condition<T> eq(T t) {
        this.eq = t;
        return this;
    }

    public Condition<T> ne(T t) {
        this.ne = t;
        return this;
    }

    public Condition<T> beNull() {
        this.Null = true;
        return this;
    }

    public Condition<T> nonNull() {
        this.Null = false;
        return this;
    }

    public Condition<T> lt(T t) {
        this.lt = t;
        return this;
    }

    public Condition<T> lte(T t) {
        this.lte = t;
        return this;
    }

    public Condition<T> gt(T t) {
        this.gt = t;
        return this;
    }

    public Condition<T> gte(T t) {
        this.gte = t;
        return this;
    }

    public Condition<T> between(T t1, T t2) {
        this.gte = t1;
        this.lte = t2;
        return this;
    }

    public Condition<T> in(List<T> tt) {
        return inList(tt);
    }

    @SuppressWarnings("unchecked")
    public final Condition<T> in(T... tt) {
        if (tt == null || tt.length == 0) {
            return this;
        } else if (tt[0] instanceof List) {
            return inList((List<T>) tt[0]);
        } else {
            return inList(Arrays.asList(tt));
        }
    }

    private Condition<T> inList(List<T> tt) {
        this.in = tt;
        return this;
    }

    public Condition<T> nin(List<T> tt) {
        return ninList(tt);
    }

    @SuppressWarnings("unchecked")
    public final Condition<T> nin(T... tt) {
        if (tt == null || tt.length == 0) {
            return this;
        } else if (tt[0] instanceof List) {
            return ninList((List<T>) tt[0]);
        } else {
            return ninList(Arrays.asList(tt));
        }
    }

    private Condition<T> ninList(List<T> tt) {
        this.nin = tt;
        return this;
    }
}
