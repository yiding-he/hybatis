package com.hyd.hybatis.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public abstract class AbstractQuery implements Query {

    private String alias;

    private List<String> projection = new ArrayList<>();

    private  List<Aggregate<?>> aggregates = new ArrayList<>();

    @Override
    public String getAlias() {
        return this.alias;
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
