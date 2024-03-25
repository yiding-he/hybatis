package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.query.column.Col;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractQuery<Q extends AbstractQuery<Q>> implements Query<Q> {

    protected String alias;

    protected List<Match> matches = new ArrayList<>();

    protected List<Column<?>> columns = new ArrayList<>();

    protected List<Column<?>> groupBy = new ArrayList<>();

    protected List<Aggregate<?>> aggregates = new ArrayList<>();

    protected int limit = -1;

    protected int offset = 0;

    @Override
    public List<Match> getMatches() {
        return this.matches;
    }

    @Override
    public List<Aggregate<?>> getAggregates() {
        return this.aggregates;
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

    public Q aggregates(List<Aggregate<?>> aggregates) {
        this.aggregates.addAll(aggregates);
        return (Q) this;
    }

    public Q aggregates(Aggregate<?>... aggregates) {
        return aggregates(List.of(aggregates));
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
