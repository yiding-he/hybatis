package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.AliasExpression;
import com.hyd.hybatis.query.column.Expression;
import com.hyd.hybatis.query.column.SimpleColumn;

public interface Column {

    Expression getExpression();

    String getName();

    default Column as(String alias) {
        return new SimpleColumn(new AliasExpression(getExpression(), alias));
    }
}
