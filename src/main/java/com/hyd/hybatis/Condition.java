package com.hyd.hybatis;

import lombok.Data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
public class Condition<T> {

    private String startsWith;   // like '...%'

    private String endsWith;     // like '%...'

    private String contains;     // like '%...%'

    /**
     * equals
     */
    private T eq;

    /**
     * not equal
     */
    private T ne;

    private Boolean Null;

    private T lt;

    private T lte;

    private T gt;

    private T gte;

    private List<T> in;

    public void startsWith(String s) {
        this.startsWith = s;
    }

    public void endsWith(String s) {
        this.endsWith = s;
    }

    public void contains(String s) {
        this.contains = s;
    }

    public void eq(T t) {
        this.eq = t;
    }

    public void ne(T t) {
        this.ne = t;
    }

    public void beNull() {
        this.Null = true;
    }

    public void nonNull() {
        this.Null = false;
    }

    public void lt(T t) {
        this.lt = t;
    }

    public void lte(T t) {
        this.lte = t;
    }

    public void gt(T t) {
        this.gt = t;
    }

    public void gte(T t) {
        this.gte = t;
    }

    public void in(List<T> tt) {
        this.in = tt;
    }

    public void in(T... tt) {
        this.in = Arrays.asList(tt);
    }
}
