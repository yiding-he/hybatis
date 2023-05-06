package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

@Data
public class DbColumn {

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
