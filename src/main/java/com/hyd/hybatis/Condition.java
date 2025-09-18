package com.hyd.hybatis;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Condition {

    private String column;

    private String operator;

    private List<Object> values;

    public Condition(String column) {
        this.column = column;
    }

    public Condition(String column, String operator, Object... values) {
        this.column = column;
        this.operator = operator;
        this.values = values == null ? Collections.emptyList() : List.of(values);
    }

    public Condition(String column, String operator, List<Object> values) {
        this.column = column;
        this.operator = operator;
        this.values = values;
    }

    public void setValue(Object value) {
        this.values = value == null ? Collections.emptyList() : List.of(value);
    }

    public Condition update(ConditionOperator operator, Object... values) {
        this.operator = operator.getClass().getSimpleName();
        if (values == null || values.length == 0) {
            this.values = Collections.emptyList();
        } else if (values.length == 1) {
            if (values[0] == null) {
                this.values = Collections.emptyList();
            } else if (values[0] instanceof Collection) {
                this.values = new ArrayList<>((Collection<?>) values[0]);
            } else {
                this.values = List.of(values[0]);
            }
        } else {
            this.values = List.of(values);
        }
        return this;
    }
}
