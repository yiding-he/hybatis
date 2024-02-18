package com.hyd.hybatis.sql;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 表示一条含参数的 SQL 语句或片段
 */
@Getter
@Setter
public class SqlCommand {

    private String statement = "";

    private List<Object> params = new ArrayList<>();

    public SqlCommand() {
    }

    public SqlCommand(String statement) {
        this.statement = statement;
    }

    public SqlCommand(String statement, List<Object> params) {
        this.statement = statement;
        this.params.addAll(params);
    }

    public SqlCommand append(String statement, List<Object> params) {
        this.statement = this.statement + statement;
        this.params.addAll(params);
        return this;
    }

    public SqlCommand append(SqlCommand command) {
        return append(command.getStatement(), command.getParams());
    }

    public SqlCommand append(String statement) {
        return append(statement, Collections.emptyList());
    }

    @Override
    public String toString() {
        return "Command{" +
                "statement='" + statement + '\'' +
                ", params=" + params +
                '}';
    }
}
