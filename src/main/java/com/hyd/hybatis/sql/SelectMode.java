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

    /**
     * 分页查询中的计数，返回总记录数
     */
    PaginationCount,

    /**
     * 分页查询中的查询结果，返回当前页的记录列表
     */
    PaginationItems
}
