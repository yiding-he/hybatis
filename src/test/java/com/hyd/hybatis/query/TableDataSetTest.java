package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableDataSetTest {

    @Test
    public void testSelectAllFromDepartments() {
        TableDataSet dataSet = new TableDataSet("DEPARTMENTS");
        SqlCommand command = dataSet.toSqlCommand();

        assertEquals("SELECT * FROM DEPARTMENTS", command.getStatement());
    }
}
