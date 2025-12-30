package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IfColumn extends AbstractColumn<IfColumn> {

    private Filter filter;

    private Column matchValue;

    private Column unmatchValue;

    public IfColumn(Filter filter, Column matchValue, Column unmatchValue) {
        this.filter = filter;
        this.matchValue = matchValue;
        this.unmatchValue = unmatchValue;
    }

    @Override
    public SqlCommand toSqlFragment() {
        return new SqlCommand("if(")
            .append(filter.toSqlFragment())
            .append(", ")
            .append(matchValue.toSqlFragment())
            .append(", ")
            .append(unmatchValue.toSqlFragment())
            .append(")");
    }
}
