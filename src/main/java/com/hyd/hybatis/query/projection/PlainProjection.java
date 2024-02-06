package com.hyd.hybatis.query.projection;

import com.hyd.hybatis.query.Projection;
import lombok.Data;

@Data
public class PlainProjection implements Projection {

    private String field;

    private String alias;

    @Override
    public String getFrom() {
        return null;
    }

    public PlainProjection with(String field) {
        this.field = field;
        return this;
    }

    public PlainProjection as(String alias) {
        this.alias = alias;
        return this;
    }
}
