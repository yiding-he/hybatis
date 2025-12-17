package com.hyd.hybatis.query;

import com.hyd.hybatis.query.match.Between;
import com.hyd.hybatis.query.match.CompositeMatch;
import com.hyd.hybatis.query.match.Equal;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

/**
 * 查询过滤条件
 */
public interface Match {

    ////////////////////////// 构建 CompositeMatch 对象的静态方法

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

    ////////////////////////// 构建 AbstractMatch 对象的静态方法

    static Equal<?> equal(Column column, Object value) {
        return new Equal<>(column, value);
    }

    static <T> Equal<T> equal(Getter<T, ?> getter, Object value) {
        return new Equal<>(getter, value);
    }

    static Between<?> between(Column column, Object min, Object max) {
        return new Between<>(column, min, max);
    }

    static <T> Between<T> between(Getter<T, ?> getter, Object min, Object max) {
        return new Between<>(getter, min, max);
    }

    //////////////////////////

    /**
     * 要过滤的字段
     */
    Column getColumn();

    /**
     * 过滤操作的目标对象，可以是字符串表达式，或 Projection
     */
    List<Object> getValues();

    SqlCommand toSqlFragment();
}
