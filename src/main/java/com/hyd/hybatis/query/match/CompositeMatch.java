package com.hyd.hybatis.query.match;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.hyd.hybatis.utils.Obj.isEmpty;

@Getter
@Setter
public class CompositeMatch implements Match {

    public enum Operator {
        AND, OR
    }

    private List<Match> matches;

    private Operator operator;

    @Override
    public Projection getProjection() {
        return null;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public SqlCommand toSqlCommand() {
        var statements = new ArrayList<String>();
        var params = new ArrayList<>();
        for (var match : matches) {
            if (match == null) {
                continue;
            }
            var sql = match.toSqlCommand();
            if (sql == null || isEmpty(sql.getStatement())) {
                continue;
            }
            statements.add("(" + sql.getStatement() + ")");
            params.addAll(sql.getParams());
        }
        return new SqlCommand("(" + String.join(operator.name(), statements) + ")", params);
    }
}
