package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.query.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractFilter implements Filter {

    private Column column;

    private List<Object> values;

}
