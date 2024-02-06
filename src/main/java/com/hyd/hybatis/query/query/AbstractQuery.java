package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class AbstractQuery implements Query {

    private String alias;

    private List<Match> matches = new ArrayList<>();

    private List<Projection> projections = new ArrayList<>();

    private List<Aggregate<?>> aggregates = new ArrayList<>();

    public void validate() {
    }
}
