package com.hyd.hybatis.query.column;

/**
 * 表示这个列是一个表达式的值
 */
public class Lit extends AbstractColumn<Lit> {

    @Override
    public String getFrom() {
        return null;
    }

    public Lit() {

    }

    public Lit(String name) {
        this.name = name;
    }
}
