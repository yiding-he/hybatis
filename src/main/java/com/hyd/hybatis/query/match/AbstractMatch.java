package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Column;
import lombok.Data;

@Data
public abstract class AbstractMatch implements Match {

    private Column<?> column;

    private Object value;

}
