package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

public class Join extends AbstractQuery<Join> {

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public List<Aggregate<?>> getAggregates() {
        return null;
    }

    @Override
    public List<Match> getMatches() {
        return null;
    }

    @Override
    public List<Projection> getProjections() {
        return null;
    }

    @Override
    public SqlCommand toSqlCommand() {
        return null;
    }
}
