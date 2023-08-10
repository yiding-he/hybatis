package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DbView implements Serializable {

    private static final long serialVersionUID = 8099016736623672428L;

    private String catalog;

    private String schema;

    private String name;

    private List<DbColumn> columns;
}
