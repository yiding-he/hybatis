package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DbMeta implements Serializable {

    private static final long serialVersionUID = -4457318599506171537L;

    private String dbProductName;

    private String dbProductVersion;

    private String schema;

    private String catalog;

    private List<DbTable> tables;
}
