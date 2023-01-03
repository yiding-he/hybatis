package com.hyd.hybatis.sql;

public enum SelectMode {

    /**
     * 普通查询，返回结果列表
     */
    Normal,

    /**
     * 计数查询，返回符合条件的记录数
     */
    Count,
}
