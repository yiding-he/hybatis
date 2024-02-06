package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Data
public abstract class AbstractQuery<Q extends AbstractQuery<Q>> implements Query<Q> {

    private String alias;

    private List<Match> matches = new ArrayList<>();

    private List<Projection> projections = new ArrayList<>();

    private List<Aggregate<?>> aggregates = new ArrayList<>();

    public void validate() {
    }

    public Q matches(List<Match> matches) {
        this.matches.addAll(matches);
        return (Q) this;
    }

    public Q matches(Match... matches) {
        return matches(List.of(matches));
    }

    public Q projections(List<Projection> projections) {
        this.projections.addAll(projections);
        return (Q) this;
    }

    public Q projections(Projection... projections) {
        return projections(List.of(projections));
    }

    public Q aggregates(List<Aggregate<?>> aggregates) {
        this.aggregates.addAll(aggregates);
        return (Q) this;
    }

    public Q aggregates(Aggregate<?>... aggregates) {
        return aggregates(List.of(aggregates));
    }
}
