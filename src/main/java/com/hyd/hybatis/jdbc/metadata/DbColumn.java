package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;

@Data
public class DbColumn implements Serializable {

    private static final long serialVersionUID = 5855722379206777828L;

    /**
     * 字段位置，0 表示第一位
     */
    private int index;

    private String name;

    private int type;

    private String typeName;

    private int size;

    private int nullable;

    private int primaryKey;

    private String remarks;

    /**
     * 字段外键约束，目前框架不支持自动收集外键约束信息
     */
    private DbFk fk;

    public boolean primaryKey() {
        return this.primaryKey > 0;
    }
}
