package com.hyd.hybatis;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Condition {

    private String column;

    private ConditionOperator operator;

    private List<Object> values;

    public Condition(String column) {
        this.column = column;
    }

    public Condition(String column, ConditionOperator operator, Object... values) {
        this.column = column;
        this.operator = operator;
        this.values = values == null ? Collections.emptyList() : List.of(values);
    }

    public Condition(String column, ConditionOperator operator, List<Object> values) {
        this.column = column;
        this.operator = operator;
        this.values = values;
    }

    public void setValue(Object value) {
        this.values = value == null ? Collections.emptyList() : List.of(value);
    }

    public Condition update(ConditionOperator operator, Object... values) {
        this.operator = operator;
        this.values = values == null ? Collections.emptyList() : List.of(values);
        return this;
    }

    //-------------------------- 静态构造方法 --------------------------

    public static Condition startsWith(String column, String value) {
        return new Condition(column, ConditionOperator.StartsWith, value);
    }

    public static Condition endsWith(String column, String value) {
        return new Condition(column, ConditionOperator.EndsWith, value);
    }

    public static Condition contains(String column, String value) {
        return new Condition(column, ConditionOperator.Contains, value);
    }

    public static Condition eq(String column, Object value) {
        return new Condition(column, ConditionOperator.Eq, value);
    }

    public static Condition ne(String column, Object value) {
        return new Condition(column, ConditionOperator.Ne, value);
    }

    public static Condition beNull(String column) {
        return new Condition(column, ConditionOperator.Null);
    }

    public static Condition beNonNull(String column) {
        return new Condition(column, ConditionOperator.NonNull);
    }

    public static Condition lt(String column, Object value) {
        return new Condition(column, ConditionOperator.Lt, value);
    }

    public static Condition lte(String column, Object value) {
        return new Condition(column, ConditionOperator.Lte, value);
    }

    public static Condition gt(String column, Object value) {
        return new Condition(column, ConditionOperator.Gt, value);
    }

    public static Condition gte(String column, Object value) {
        return new Condition(column, ConditionOperator.Gte, value);
    }

    public static Condition between(String column, Object value1, Object value2) {
        return new Condition(column, ConditionOperator.Between, value1, value2);
    }

    public static Condition in(String column, Object... values) {
        return new Condition(column, ConditionOperator.In, values);
    }

    public static Condition in(String column, List<Object> values) {
        return new Condition(column, ConditionOperator.In, values);
    }

    public static Condition nin(String column, Object... values) {
        return new Condition(column, ConditionOperator.Nin, values);
    }

    public static Condition nin(String column, List<Object> values) {
        return new Condition(column, ConditionOperator.Nin, values);
    }

    public static Condition orderAsc(String column, int index) {
        return new Condition(column, ConditionOperator.OrderAsc, index);
    }

    public static Condition orderDesc(String column, int index) {
        return new Condition(column, ConditionOperator.OrderDesc, index);
    }
}
