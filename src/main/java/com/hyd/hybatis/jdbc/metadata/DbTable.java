package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class DbTable implements Serializable {

    private static final long serialVersionUID = 4040779844456762005L;

    private String catalog;

    private String schema;

    private String name;

    private String remarks;

    private List<DbColumn> columns;

    ///////////////////////////////

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

    public Optional<DbColumn> findColumn(String columnName) {
        return this.columns.stream()
            .filter(c -> c.getName().equalsIgnoreCase(columnName))
            .findFirst();
    }
}
