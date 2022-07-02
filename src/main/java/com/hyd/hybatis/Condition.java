package com.hyd.hybatis;

import lombok.Data;

import java.util.Arrays;
import java.util.Collection;

@Data
public class Condition<T> {

    private String startsWith;   // like '...%'

    public Condition<T> startsWith(String s) {
        this.startsWith = s;
        return this;
    }

    private String endsWith;     // like '%...'

    public Condition<T> endsWith(String s) {
        this.endsWith = s;
        return this;
    }

    private String contains;     // like '%...%'

    public Condition<T> contains(String s) {
        this.contains = s;
        return this;
    }

    /**
     * equals
     */
    private T eq;

    public Condition<T> eq(T t) {
        this.eq = t;
        return this;
    }

    /**
     * not equal
     */
    private T ne;

    public Condition<T> ne(T t) {
        this.ne = t;
        return this;
    }

    private T lt;

    public Condition<T> lt(T t) {
        this.lt = t;
        return this;
    }

    private T lte;

    public Condition<T> lte(T t) {
        this.lte = t;
        return this;
    }

    private T gt;

    public Condition<T> gt(T t) {
        this.gt = t;
        return this;
    }

    private T gte;

    public Condition<T> gte(T t) {
        this.gte = t;
        return this;
    }

    private Collection<T> in;

    public Condition<T> in(Collection<T> tt) {
        this.in = tt;
        return this;
    }

    public Condition<T> in(T... tt) {
        this.in = Arrays.asList(tt);
        return this;
    }
}
