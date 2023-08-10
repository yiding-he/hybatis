package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Data
public class DbMeta implements Serializable {

    private static final long serialVersionUID = -4457318599506171537L;

    private String dbProductName;

    private String dbProductVersion;

    private String schema;

    private String catalog;

    private List<DbTable> tables;

    private List<DbView> views;

    ///////////////////////////////

    public Optional<DbTable> findTable(String tableName) {
        return tables.stream()
            .filter(t -> t.getName().equalsIgnoreCase(tableName))
            .findFirst();
    }

    public Optional<DbColumn> findColumn(String tableName, String columnName) {
        return findTable(tableName).flatMap(t -> t.findColumn(columnName));
    }
}
