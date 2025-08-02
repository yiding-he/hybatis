package com.hyd.hybatis.sql;

import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
public class SqlCommand {

    private String statement;

    private List<Object> params;

    public SqlCommand() {
    }

    public SqlCommand(String statement, List<Object> params) {
        this.statement = statement;
        this.params = params;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setParams(List<Object> params) {
        this.params = params;
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
