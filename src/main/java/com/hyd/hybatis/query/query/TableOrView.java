package com.hyd.hybatis.query.query;

import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Str;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.util.Collections.emptyList;

/**
 * 直接从表或视图中进行查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableOrView extends AbstractQuery<TableOrView> {

    private String name;

    public TableOrView() {
    }

    public TableOrView(String name) {
        this.name = name;
    }

    @Override
    public String getAlias() {
        return Str.firstNonBlank(super.getAlias(), this.name);
    }


    @Override
    public SqlCommand getFromFragment() {
        return new SqlCommand(getName(), emptyList());
    }
}
