package com.hyd.hybatis.query.column;

/**
 * 表示这个列来自一个聚合操作
 */
public class Agg extends AbstractColumn<Agg> {

    @Override
    public String getFrom() {
        // 聚合操作没有单一来源
        return null;
    }
}
