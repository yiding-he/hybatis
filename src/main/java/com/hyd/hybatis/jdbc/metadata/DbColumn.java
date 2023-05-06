package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;

@Data
public class DbColumn implements Serializable {

    private static final long serialVersionUID = 5855722379206777828L;

    private String name;

    private int type;

    private String typeName;

    private int size;

    private int nullable;

    private int primaryKey;

    private String remarks;

    public boolean primaryKey() {
        return this.primaryKey > 0;
    }
}
