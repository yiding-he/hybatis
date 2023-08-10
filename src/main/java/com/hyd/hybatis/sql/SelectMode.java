package com.hyd.hybatis.sql;

/**
 * The pattern of the query method in the Mapper interface. If the return value of the query method
 * is a collection type, then it is Normal mode. If the return value of the query method is a Number
 * type, then it is Count mode.
 */
public enum SelectMode {

    /**
     * Normal mode, which means return a list of records.
     */
    Normal,

    /**
     * Counting mode, which means return number of matching records.
     */
    Count,
}
