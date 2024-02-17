package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static com.hyd.hybatis.utils.Obj.defaultValue;
import static com.hyd.hybatis.utils.Obj.isNotEmpty;

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
    public SqlCommand getFromSegment() {
        return this.from.toSqlCommand();
    }

    @Override
    public SqlCommand toSqlCommand() {
        var subQuery = from.toSqlCommand();
        var statement = "SELECT " + defaultValue(getProjectionsStatement(), "*") +
            " FROM (" + subQuery.getStatement() + ")" + appendAlias();

        final var params = new ArrayList<>(subQuery.getParams());
        if (isNotEmpty(this.getMatches())) {
            var matchSql = Match.AND(this.getMatches()).toSqlCommand();
            statement += " WHERE " + matchSql.getStatement();
            params.addAll(matchSql.getParams());
        }

        return new SqlCommand(statement, params);
    }
}
