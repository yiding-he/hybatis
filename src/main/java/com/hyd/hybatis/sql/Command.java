package com.hyd.hybatis.sql;

import java.util.List;

public class Command {

    private String statement;

    private List<Object> params;

    public Command() {
    }

    public Command(String statement, List<Object> params) {
        this.statement = statement;
        this.params = params;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Command{" +
                "statement='" + statement + '\'' +
                ", params=" + params +
                '}';
    }
}
