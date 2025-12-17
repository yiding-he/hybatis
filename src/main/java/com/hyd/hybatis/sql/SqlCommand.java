package com.hyd.hybatis.sql;

import com.hyd.hybatis.query.Column;
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

    /**
     * 拼凑后续内容，其中 value 有可能是 Column 对象
     *
     * @param statement 只包含一个占位符的语句
     * @param value     占位符对应的值，可能是 Column 对象
     */
    public SqlCommand appendMaybeColumn(String statement, Object value) {
        if (value instanceof Column c) {
            return append(statement.replace("?", "")).append(c.toSqlFragment());
        } else {
            return append(statement, List.of(value));
        }
    }

    public SqlCommand clone() {
        SqlCommand newInstance;
        try {
            newInstance = (SqlCommand) super.clone();
        } catch (CloneNotSupportedException e) {
            newInstance = new SqlCommand();
        }
        newInstance.statement = this.statement;
        newInstance.params = this.params;
        return newInstance;
    }

    /**
     * 合并两个 SqlCommand，并返回一个新对象
     */
    public SqlCommand concat(SqlCommand another) {
        var clone = clone();
        if (another != null) {
            clone.statement += another.statement;
            clone.params.addAll(another.params);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "Command{" +
            "statement='" + statement + '\'' +
            ", params=" + params +
            '}';
    }
}
