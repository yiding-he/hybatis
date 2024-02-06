package com.hyd.hybatis.query.aggregate;

import com.hyd.hybatis.query.Aggregate;
import com.hyd.hybatis.query.Projection;
import lombok.Data;

@Data
public abstract class AbstractAggregate<A extends AbstractAggregate<A>> implements Aggregate<A> {

    private Projection projection;

    private String alias;

    @SuppressWarnings("unchecked")
    public A as(String alias) {
        this.alias = alias;
        return (A) this;
    }
}
