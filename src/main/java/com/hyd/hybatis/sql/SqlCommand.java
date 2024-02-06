package com.hyd.hybatis.sql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 表示一条含参数的 SQL 语句或片段
 */
@Getter
@Setter
public class SqlCommand {

    private String statement;

    private List<Object> params;

    public SqlCommand() {
    }

    public SqlCommand(String statement, List<Object> params) {
        this.statement = statement;
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
