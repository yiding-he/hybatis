package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.Setter;

/**
 * 从子 Query 中查询
 */
@Getter @Setter
public class Wrap extends AbstractQuery<Wrap> {

    protected Query<?> from;

    public Wrap(Query<?> from) {
        this.from = from;
    }

    public Wrap() {
    }

    @Override
    public SqlCommand getFromFragment() {
        return new SqlCommand("(").append(this.from.toSqlCommand()).append(")");
    }

}
