package com.hyd.hybatis.query;

public interface Aggregate<A extends Aggregate<A>> {

    String getColumn();

    String getAlias();

    String getPartitionBy();
}
