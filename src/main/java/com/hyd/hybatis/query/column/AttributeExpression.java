package com.hyd.hybatis.query.column;

import com.hyd.hybatis.sql.SqlCommand;

public class AttributeExpression extends Expression {

    private final String name;

    private String dataSetAlias;

    private Long dataSetId;

    public AttributeExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDataSetAlias() {
        return dataSetAlias;
    }

    public void setDataSetAlias(String dataSetAlias) {
        this.dataSetAlias = dataSetAlias;
    }

    public Long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Override
    public SqlCommand toSqlCommand() {
        var cmd = new SqlCommand();
        if (dataSetAlias != null && !dataSetAlias.isEmpty()) {
            cmd.append(dataSetAlias).append(".");
        }
        return cmd.append(name);
    }
}
