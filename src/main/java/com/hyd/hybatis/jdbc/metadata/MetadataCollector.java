package com.hyd.hybatis.jdbc.metadata;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    ////////////////////////////////////////

    private List<DbView> collectViews(DatabaseMetaData metaData, Context context) throws SQLException {
        List<DbView> viewList = new ArrayList<>();
        try (var views = metaData.getTables(context.catalog, context.schema, null, new String[]{"VIEW"})) {
            while (views.next()) {
                var view = new DbView();
                view.setName(views.getString("TABLE_NAME"));
                view.setCatalog(context.catalog);
                view.setSchema(context.schema);
                view.setColumns(collectColumns(metaData, context, view.getName()));
                viewList.add(view);
            }
        }
        return viewList;
    }

    ////////////////////////////////////////

    private List<DbTable> collectTables(DatabaseMetaData metaData, Context context) throws SQLException {
        List<DbTable> tableList = new ArrayList<>();
        try (var tables = metaData.getTables(context.catalog, context.schema, null, new String[] {"TABLE"})) {
            while (tables.next()) {
                var table = new DbTable();
                table.setName(tables.getString("TABLE_NAME"));
                table.setRemarks(tables.getString("REMARKS"));
                table.setCatalog(context.catalog);
                table.setSchema(context.schema);
                table.setColumns(collectColumns(metaData, context, table.getName()));
                tableList.add(table);
            }
        }
        return tableList;
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
        try (var columns = metaData.getColumns(context.catalog, context.schema, tableName, null)) {
            while (columns.next()) {
                var column = new DbColumn();
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
