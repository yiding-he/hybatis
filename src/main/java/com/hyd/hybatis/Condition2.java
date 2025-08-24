package com.hyd.hybatis;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Condition2 {

    private String column;

    private ConditionOperator operator;

    private List<Object> values;

    public void setValue(Object value) {
        this.values = value == null ? Collections.emptyList() : List.of(value);
    }
}
