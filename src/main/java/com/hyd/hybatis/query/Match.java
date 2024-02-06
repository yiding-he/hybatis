package com.hyd.hybatis.query;

import com.hyd.hybatis.query.match.CompositeMatch;
import com.hyd.hybatis.query.match.Equal;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

/**
 * 查询过滤条件
 */
public interface Match {

    static CompositeMatch AND(List<Match> matches) {
        var compositeMatch = new CompositeMatch();
        compositeMatch.setOperator(CompositeMatch.Operator.AND);
        compositeMatch.setMatches(matches);
        return compositeMatch;
    }

    static CompositeMatch AND(Match... matches) {
        return AND(List.of(matches));
    }

    static CompositeMatch OR(List<Match> matches) {
        var compositeMatch = new CompositeMatch();
        compositeMatch.setOperator(CompositeMatch.Operator.OR);
        compositeMatch.setMatches(matches);
        return compositeMatch;
    }

    static CompositeMatch OR(Match... matches) {
        return OR(List.of(matches));
    }

    static Equal equal(String field, Object value) {
        return new Equal(field, value);
    }

    /**
     * 要过滤的字段
     */
    String getField();

    /**
     * 过滤操作的目标对象，可以是字符串表达式，或 Projection
     */
    Object getValue();

    SqlCommand toSqlCommand();
}
