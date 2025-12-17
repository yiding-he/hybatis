package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractMatch implements Match {

    private Column column;

    private List<Object> values;

}
