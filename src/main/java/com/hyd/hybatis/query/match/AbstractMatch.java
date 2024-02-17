package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import lombok.Data;

@Data
public abstract class AbstractMatch implements Match {

    private Projection projection;

    private Object value;

}
