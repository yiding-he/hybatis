package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IfColumn extends AbstractColumn<IfColumn> {

    private Match match;

    private Column matchValue;

    private Column unmatchValue;

    public IfColumn(Match match, Column matchValue, Column unmatchValue) {
        this.match = match;
        this.matchValue = matchValue;
        this.unmatchValue = unmatchValue;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand("if(")
            .append(match.toSqlFragment())
            .append(", ")
            .append(matchValue.toSqlFragment())
            .append(", ")
            .append(unmatchValue.toSqlFragment())
            .append(")");
    }
}
