package com.hyd.hybatis.query;

import com.hyd.hybatis.utils.Str;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableOrView extends AbstractQuery {

    private String name;

    @Override
    public String getAlias() {
        return Str.firstNonBlank(super.getAlias(), this.name);
    }
}
