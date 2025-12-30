package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.query.query.AbstractQuery;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Data;

import static com.hyd.hybatis.query.Column.lit;

@Data
public class Exists implements Filter {

    private AbstractQuery<?> query;

    public Exists(AbstractQuery<?> query) {
        this.query = query;
    }

    @Override
    public Column getColumn() {
        return null;
    }

    @Override
    public java.util.List<Object> getValues() {
        return null;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand("EXISTS(")
            .append(this.query.columns(lit(1)).toSqlCommand())
            .append(")");
    }
}
