package com.hyd.hybatis.query.aggregate;

import com.hyd.hybatis.query.Column;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public abstract class SingleColumnAggregate<S extends SingleColumnAggregate<S>> extends AbstractAggregate<S> {

    protected Column<?> column;
}
