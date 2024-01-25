package com.hyd.hybatis.query;

import java.util.List;

public interface Query {

    String getAlias();

    List<Aggregate<?>> getAggregates();

    List<String> getProjection();

}
