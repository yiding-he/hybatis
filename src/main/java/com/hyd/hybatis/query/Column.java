package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.AliasExpression;
import com.hyd.hybatis.query.column.Expression;
import com.hyd.hybatis.query.column.SimpleColumn;
import com.hyd.hybatis.query.filter.BinaryFilter;

public interface Column {

    Expression getExpression();

    String getName();

    default Column as(String alias) {
        return new SimpleColumn(new AliasExpression(getExpression(), alias));
    }

    default Filter equalTo(Column other) {
        return new BinaryFilter(this, other, "=");
    }

    default Filter equal(Column other) {
        return equalTo(other);
    }

    default Filter notEqual(Column other) {
        return new BinaryFilter(this, other, "!=");
    }

    default Filter greaterThan(Column other) {
        return new BinaryFilter(this, other, ">");
    }

    default Filter lessThan(Column other) {
        return new BinaryFilter(this, other, "<");
    }

    default Filter greaterThanOrEqual(Column other) {
        return new BinaryFilter(this, other, ">=");
    }

    default Filter lessThanOrEqual(Column other) {
        return new BinaryFilter(this, other, "<=");
    }
}
