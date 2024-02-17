package com.hyd.hybatis.query;

/**
 * 带别名的结构，提供 appendAlias() 方法
 */
public interface Alias {

    String getAlias();

    default String appendAlias() {
        return getAlias() == null ? "" : (" AS " + getAlias());
    }
}
