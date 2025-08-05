package com.hyd.hybatis.jdbc.metadata;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The cached version of the DbMeta class has two purposes:
 * first, if database metadata needs to be preloaded at startup,
 * using cached lazy loading can reduce startup time;
 * second, implementing cache expiration times allows for
 * automatically updating the database metadata.
 */
@Slf4j
public class CachingDbMeta {

    private final MetadataCollector metadataCollector;

    private final LoadingCache<String, DbTable> tableCache;

    private final LoadingCache<String, DbView> viewCache;

    //////////////////////////////////////// Constructors

    public CachingDbMeta(DataSource dataSource, long ttlMillis) {
        this(new MetadataCollector(dataSource), ttlMillis);
    }

    public CachingDbMeta(MetadataCollector metadataCollector, long ttlMillis) {
        this.metadataCollector = metadataCollector;

        this.tableCache = Caffeine.newBuilder()
            .expireAfterWrite(ttlMillis, TimeUnit.MILLISECONDS)
            .build(this::loadTable);

        this.viewCache = Caffeine.newBuilder()
            .expireAfterWrite(ttlMillis, TimeUnit.MILLISECONDS)
            .build(this::loadView);
    }

    ////////////////////////////////////////

    public DbTable getTable(String tableName) {
        return this.tableCache.get(tableName);
    }

    public DbView getView(String viewName) {
        return this.viewCache.get(viewName);
    }

    public Optional<DbTable> findTable(String tableName) {
        return Optional.ofNullable(tableCache.get(tableName));
    }

    public Optional<DbColumn> findColumn(String tableName, String columnName) {
        return findTable(tableName).flatMap(t -> t.findColumn(columnName));
    }

    ////////////////////////////////////////

    private DbTable loadTable(String tableName) {
        try {
            log.debug("Loading metadata for table {}", tableName);
            return this.metadataCollector.collectTable(tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DbView loadView(String viewName) {
        try {
            log.debug("Loading metadata for view {}", viewName);
            return this.metadataCollector.collectView(viewName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
