package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;
import com.hyd.hybatis.utils.Str;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

import static com.hyd.hybatis.utils.Obj.defaultValue;
import static com.hyd.hybatis.utils.Obj.isNotEmpty;
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
    public SqlCommand getFromSegment() {
        return new SqlCommand(getName(), emptyList());
    }

    @Override
    public void validate() {
        getColumns().stream().filter(p -> Obj.isNotEmpty(p.getFrom())).forEach(p -> {
            if (!p.getFrom().equals(getAlias())) {
                throw new IllegalArgumentException("Projection '" + p.toSqlExpression() +
                    "' cannot be selected from table or view '" + getAlias() + "'"
                );
            }
        });
    }
}
