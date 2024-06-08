package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Query;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Join extends AbstractQuery<Join> {

    public enum JoinType {
        Left, Right, Inner
    }

    private Query<?> leftQuery;

    private Query<?> rightQuery;

    private JoinType joinType = JoinType.Left;

    private Set<String> joinColumns;

    public Join() {
    }

    public Join(Query<?> leftQuery, Query<?> rightQuery, JoinType joinType, String... joinColumns) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.joinType = joinType;
        this.joinColumns = Set.of(joinColumns);
    }

    @Override
    public SqlCommand getFromFragment() {
        var leftCommand = leftQuery.getFromFragment();
        var rightCommand = rightQuery.getFromFragment();
        var match = Match.AND(
            joinColumns.stream()
                .map(column -> Match.equal(leftQuery.col(column), rightQuery.col(column)))
                .collect(Collectors.toList())
        );
        return new SqlCommand()
            .append("(")
            .append(leftCommand.getStatement(), leftCommand.getParams())
            .append(") " + leftQuery.appendAlias())
            .append(" " + joinType.name().toUpperCase() + " JOIN (")
            .append(rightCommand.getStatement(), rightCommand.getParams())
            .append(") " + rightQuery.appendAlias() + " ON ")
            .append(match.toSqlFragment())
            ;
    }
}
