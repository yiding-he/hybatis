package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DbTable {

    private String catalog;

    private String schema;

    private String name;

    private String remarks;

    private List<DbColumn> columns;

    public List<DbColumn> primaryKeyColumns() {
        return this.columns == null? Collections.emptyList():
            this.columns.stream()
                .filter(DbColumn::primaryKey)
                .sorted(Comparator.comparing(DbColumn::getPrimaryKey))
                .collect(Collectors.toList());
    }

    public List<String> primaryKeyNames() {
        return primaryKeyColumns().stream()
            .map(DbColumn::getName)
            .collect(Collectors.toList());
    }
}
