package com.hyd.hybatis.query;

import com.hyd.hybatis.query.column.JoinCondition;
import com.hyd.hybatis.query.column.SimpleColumn;
import com.hyd.hybatis.query.column.AttributeExpression;
import com.hyd.hybatis.sql.SqlCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JoinDataSetTest {

    @Test
    public void testJoinDepartmentsAndDeptEmp() {
        TableDataSet departments = new TableDataSet("DEPARTMENTS");
        TableDataSet deptEmp = new TableDataSet("DEPT_EMP");

        SimpleColumn deptNoFromDept = new SimpleColumn(new AttributeExpression("DEPARTMENTS.dept_no"));
        SimpleColumn deptNoFromDeptEmp = new SimpleColumn(new AttributeExpression("DEPT_EMP.dept_no"));

        JoinDataSet joinDataSet = new JoinDataSet(
            departments,
            deptEmp,
            JoinDataSet.JoinType.INNER,
            new JoinCondition(deptNoFromDept, deptNoFromDeptEmp,
                JoinCondition.JoinOperator.EQUAL)
        );

        SqlCommand command = joinDataSet.toSqlCommand();

        assertEquals("SELECT * FROM DEPARTMENTS INNER JOIN DEPT_EMP ON DEPARTMENTS.dept_no = DEPT_EMP.dept_no",
            command.getStatement());
    }
}
