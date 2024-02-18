package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.Lit;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Str;

import java.util.Collections;

/**
 * 表示字段
 */
public interface Column<C extends Column<C>> extends Alias {

    /**
     * 以表达式作为字段
     */
    static Lit lit(String expression) {
        return new Lit(expression);
    }

    /**
     * 字段来源，如果来自 Query，则取其别名；如果是表达式，则没有来源
     */
    String getFrom();

    /**
     * 字段名或表达式
     */
    String getName();

    // TODO 字段表达式也应该是可以带参数的

    default SqlCommand toSqlFragment() {
        return new SqlCommand(
            (Str.isBlank(getFrom()) ? "" : (getFrom() + ".")) + getName() + appendAlias(),
            Collections.emptyList()
        );
    }
}
