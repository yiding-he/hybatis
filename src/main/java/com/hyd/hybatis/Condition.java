package com.hyd.hybatis;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class Condition<T> implements Serializable {

    private static final long serialVersionUID = 43211L;

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
        this.in = tt;
        return this;
    }

    public Condition<T> in(T... tt) {
        this.in = Arrays.asList(tt);
        return this;
    }
}
