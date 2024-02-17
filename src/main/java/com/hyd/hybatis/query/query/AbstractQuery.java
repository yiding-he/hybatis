package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Query;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractQuery<Q extends AbstractQuery<Q>> implements Query<Q> {

    protected String alias;

    protected List<Match> matches = new ArrayList<>();

    protected List<Column<?>> columns = new ArrayList<>();

    protected List<Aggregate<?>> aggregates = new ArrayList<>();

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

    public void validate() {
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
}
