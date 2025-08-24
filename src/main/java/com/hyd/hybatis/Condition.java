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

    public void setValue(Object value) {
        this.values = value == null ? Collections.emptyList() : List.of(value);
    }

    public Condition update(ConditionOperator operator, Object... values) {
        this.operator = operator;
        this.values = values == null ? Collections.emptyList() : List.of(values);
        return this;
    }
}
