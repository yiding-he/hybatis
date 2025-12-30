package com.hyd.hybatis.query;

import com.hyd.hybatis.query.filter.*;
import com.hyd.hybatis.query.query.AbstractQuery;
import com.hyd.hybatis.sql.SqlCommand;

import java.util.List;

/**
 * 查询过滤条件
 */
public interface Filter {

    // --------------------- 构建 CompositeMatch 对象的静态方法 ---------------------

    static CompositeFilter AND(List<Filter> filters) {
        var compositeMatch = new CompositeFilter();
        compositeMatch.setOperator(CompositeFilter.Operator.AND);
        compositeMatch.setFilters(filters);
        return compositeMatch;
    }

    static CompositeFilter AND(Filter... filters) {
        return AND(List.of(filters));
    }

    static CompositeFilter OR(List<Filter> filters) {
        var compositeMatch = new CompositeFilter();
        compositeMatch.setOperator(CompositeFilter.Operator.OR);
        compositeMatch.setFilters(filters);
        return compositeMatch;
    }

    static CompositeFilter OR(Filter... filters) {
        return OR(List.of(filters));
    }

    static CompositeFilter NOT(Filter filter) {
        var compositeMatch = new CompositeFilter();
        compositeMatch.setOperator(CompositeFilter.Operator.NOT);
        compositeMatch.setFilters(List.of(filter));
        return compositeMatch;
    }

    // --------------------- 构建 AbstractMatch 对象的静态方法 ---------------------

    static Equal<?> equal(Column column, Object value) {
        return new Equal<>(column, value);
    }

    static <T> Equal<T> equal(Getter<T, ?> getter, Object value) {
        return new Equal<>(getter, value);
    }

    static LowerThan<?> lowerThan(Column column, Object value) {
        return new LowerThan<>(column, value);
    }

    static <T> LowerThan<T> lowerThan(Getter<T, ?> getter, Object value) {
        return new LowerThan<>(getter, value);
    }

    static LowerThanOrEqual<?> lowerThanOrEqual(Column column, Object value) {
        return new LowerThanOrEqual<>(column, value);
    }

    static <T> LowerThanOrEqual<T> lowerThanOrEqual(Getter<T, ?> getter, Object value) {
        return new LowerThanOrEqual<>(getter, value);
    }

    static GreaterThan<?> greaterThan(Column column, Object value) {
        return new GreaterThan<>(column, value);
    }

    static <T> GreaterThan<T> greaterThan(Getter<T, ?> getter, Object value) {
        return new GreaterThan<>(getter, value);
    }

    static GreaterThanOrEqual<?> greaterThanOrEqual(Column column, Object value) {
        return new GreaterThanOrEqual<>(column, value);
    }

    static <T> GreaterThanOrEqual<T> greaterThanOrEqual(Getter<T, ?> getter, Object value) {
        return new GreaterThanOrEqual<>(getter, value);
    }

    static Between<?> between(Column column, Object min, Object max) {
        return new Between<>(column, min, max);
    }

    static <T> Between<T> between(Getter<T, ?> getter, Object min, Object max) {
        return new Between<>(getter, min, max);
    }

    static In<?> in(Column column, List<Object> values) {
        return new In<>(column, values);
    }

    static <T> In<T> in(Getter<T, ?> getter, List<Object> values) {
        return new In<>(getter, values);
    }

    static NotIn<Object> notIn(Column column, List<Object> values) {
        return new NotIn<>(column, values);
    }

    static <T> NotIn<T> notIn(Getter<T, ?> getter, List<Object> values) {
        return new NotIn<>(getter, values);
    }

    static Exists exists(AbstractQuery<?> query) {
        return new Exists(query);
    }

    // -----------------------------------------------------

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
