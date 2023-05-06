package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.util.List;

@Data
public class DbMeta {

    private String dbProductName;

    private String dbProductVersion;

    private String schema;

    private String catalog;

    private List<DbTable> tables;
}
