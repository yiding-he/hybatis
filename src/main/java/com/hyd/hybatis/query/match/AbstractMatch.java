package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Data;

@Data
public abstract class AbstractMatch implements Match {

    private String field;

    private Object value;

}
