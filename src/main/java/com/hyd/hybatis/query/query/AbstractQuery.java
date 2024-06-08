package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一个基本的查询结构，最终可以在数据库中执行查询，得到一个二维表结果。它有三个基本功能：
 * 1. 可执行。AbstractQuery 包含基本的 SELECT...FROM...WHERE... 结构要素，可用于生成完整的 SQL 语句；
 * 2. 可嵌套。AbstractQuery 可以嵌套在另一个 AbstractQuery 的 FROM 结构中；
 * 3. 可连接。多个 AbstractQuery 可以通过 JOIN 关联起来。
 * 基于这三个基本功能，我们可以构建具有高度可复用的查询结构。
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQuery<Q extends AbstractQuery<Q>> implements Query<Q> {

    /**
     * 本查询的别名。当嵌入另一个查询时，外部查询可以通过别名来引用本查询。
     */
    protected String alias;

    /**
     * 过滤条件
     */
    protected List<Match> matches = new ArrayList<>();

    /**
     * 本查询提供哪些字段
     * 字段可以是直接选取的，也可以是聚合操作等等
     */
    protected List<Column<?>> columns = new ArrayList<>();

    /**
     * 聚合维度，会出现在本查询提供的字段列表中
     */
    protected List<Column<?>> groupBy = new ArrayList<>();

    protected int limit = -1;

    protected int offset = 0;

    @Override
    public List<Match> getMatches() {
        return this.matches;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public List<Column<?>> getColumns() {
        return columns;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public List<Column<?>> getGroupBy() {
        return groupBy;
    }

    public Q matches(List<Match> matches) {
        this.matches.addAll(matches);
        return (Q) this;
    }

    public Q matches(Match... matches) {
        return matches(List.of(matches));
    }

    public Q columns(List<Column<?>> columns) {
        this.columns.addAll(columns);
        return (Q) this;
    }

    public Q columns(Column<?>... columns) {
        return columns(List.of(columns));
    }

    public Q columnNames(List<String> columns) {
        return columns(columns.stream().map(this::col).collect(Collectors.toList()));
    }

    public Q columnNames(String... columns) {
        return columnNames(List.of(columns));
    }

    public Q groupBy(List<Column<?>> columns) {
        this.groupBy.addAll(columns);
        return (Q) this;
    }

    public Q groupBy(Column<?>... columns) {
        return groupBy(List.of(columns));
    }

    public Q as(String alias) {
        this.alias = alias;
        return (Q) this;
    }

    public Q limit(int limit) {
        this.limit = limit;
        return (Q) this;
    }

    public Q skip(int skip) {
        this.offset = skip;
        return (Q) this;
    }
}
