package com.hyd.hybatis.query;

import java.util.List;

public class Join implements Query{

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public List<Aggregate<?>> getAggregates() {
        return null;
    }

    @Override
    public List<String> getProjection() {
        return null;
    }
}
