package com.hyd.hybatis.jdbc.metadata;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Collect metadata from database via JDBC API.
 */
public class MetadataCollector {

    private static class Context {

        private final String catalog;

        private final String schema;

        private Context(String catalog, String schema) {
            this.catalog = catalog;
            this.schema = schema;
        }
    }

    @FunctionalInterface
    private interface MetadataProcessor<T> {

        T process(DatabaseMetaData metaData, Context context) throws SQLException;
    }

    private final DataSource dataSource;

    public MetadataCollector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbMeta collect() throws SQLException {
        try (var connection = this.dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            return collect(metaData, new Context(connection.getCatalog(), connection.getSchema()));
        }
    }

    public DbMeta collect(String catalog, String schema) throws SQLException {
        try (var connection = this.dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            return collect(metaData, new Context(catalog, schema));
        }
    }

    private DbMeta collect(DatabaseMetaData metaData, Context context) throws SQLException {
        var dbMeta = new DbMeta();
        dbMeta.setDbProductName(metaData.getDatabaseProductName());
        dbMeta.setDbProductVersion(dbMeta.getDbProductVersion());
        dbMeta.setCatalog(context.catalog);
        dbMeta.setSchema(context.schema);
        dbMeta.setTables(collectTables(metaData, context));
        dbMeta.setViews(collectViews(metaData, context));
        return dbMeta;
    }

    private <T> T withMetadata(MetadataProcessor<T> processor) throws SQLException {
        try (var connection = this.dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            return processor.process(metaData, new Context(connection.getCatalog(), connection.getSchema()));
        }
    }

    ////////////////////////////////////////

    public DbView collectView(String viewName) throws SQLException {
        return withMetadata((metaData, context) -> collectView0(viewName, metaData, context));
    }

    private List<DbView> collectViews(DatabaseMetaData metaData, Context context) throws SQLException {
        List<DbView> viewList = new ArrayList<>();
        try (var views = metaData.getTables(context.catalog, context.schema, null, new String[]{"VIEW"})) {
            while (views.next()) {
                viewList.add(collectView0(
                    views.getString("TABLE_NAME"), metaData, context
                ));
            }
        }
        return viewList;
    }

    /**
     * Reading database view information
     */
    private DbView collectView0(String viewName, DatabaseMetaData metaData, Context context) throws SQLException {
        var view = new DbView();
        view.setName(viewName);
        view.setCatalog(context.catalog);
        view.setSchema(context.schema);
        view.setColumns(collectColumns(metaData, context, view.getName()));
        return view;
    }

    ////////////////////////////////////////

    public DbTable collectTable(String tableName) throws SQLException {
        return withMetadata((metaData, context) -> {
            try (var tables = metaData.getTables(context.catalog, context.schema, tableName, null)) {
                if (tables.next()) {
                    return collectTable0(metaData, context, tables);
                }
            }
            return null;
        });
    }

    private List<DbTable> collectTables(DatabaseMetaData metaData, Context context) throws SQLException {
        List<DbTable> tableList = new ArrayList<>();
        try (var tables = metaData.getTables(context.catalog, context.schema, null, new String[] {"TABLE"})) {
            while (tables.next()) {
                var table = collectTable0(metaData, context, tables);
                tableList.add(table);
            }
        }
        return tableList;
    }

    private DbTable collectTable0(DatabaseMetaData metaData, Context context, ResultSet tableRs) throws SQLException {
        var table = new DbTable();
        table.setName(tableRs.getString("TABLE_NAME"));
        table.setRemarks(tableRs.getString("REMARKS"));
        table.setCatalog(context.catalog);
        table.setSchema(context.schema);
        table.setColumns(collectColumns(metaData, context, table.getName()));
        return table;
    }

    ////////////////////////////////////////

    private List<DbColumn> collectColumns(
        DatabaseMetaData metaData, Context context, String tableName
    ) throws SQLException {
        Map<String, Integer> primaryKeyMap = new HashMap<>();
        try (var primaryKeys = metaData.getPrimaryKeys(context.catalog, context.schema, tableName)) {
            while (primaryKeys.next()) {
                var columnName = primaryKeys.getString("COLUMN_NAME");
                primaryKeyMap.put(columnName, primaryKeyMap.size() + 1);
            }
        }

        List<DbColumn> columnList = new ArrayList<>();
        var counter = new AtomicInteger();
        try (var columns = metaData.getColumns(context.catalog, context.schema, tableName, null)) {
            while (columns.next()) {
                var column = new DbColumn();
                column.setIndex(counter.getAndIncrement());
                column.setName(columns.getString("COLUMN_NAME"));
                column.setRemarks(columns.getString("REMARKS"));
                column.setType(columns.getInt("DATA_TYPE"));
                column.setTypeName(columns.getString("TYPE_NAME"));
                column.setSize(columns.getInt("COLUMN_SIZE"));
                column.setNullable(columns.getInt("NULLABLE"));
                column.setPrimaryKey(primaryKeyMap.getOrDefault(column.getName(), 0));
                columnList.add(column);
            }
        }
        return columnList;
    }
}
