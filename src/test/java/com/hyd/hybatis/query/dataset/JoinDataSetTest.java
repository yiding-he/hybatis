package com.hyd.hybatis.query.dataset;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.DataSet;
import com.hyd.hybatis.sql.SqlCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JoinDataSetTest {

    @Test
    public void testJoinDepartmentsAndDeptEmp() {
        TableDataSet departments = new TableDataSet("DEPARTMENTS");
        TableDataSet deptEmp = new TableDataSet("DEPT_EMP");

        Column deptNoFromDept = departments.col("dept_no");
        Column deptNoFromDeptEmp = deptEmp.col("dept_no");

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

    @Test
    public void testJoinWithAlias() {
        TableDataSet departments = new TableDataSet("DEPARTMENTS").as("d");
        TableDataSet deptEmp = new TableDataSet("DEPT_EMP").as("e");

        Column deptNoFromDept = departments.col("dept_no");
        Column deptNoFromDeptEmp = deptEmp.col("dept_no");

        JoinDataSet joinDataSet = new JoinDataSet(
            departments,
            deptEmp,
            JoinDataSet.JoinType.INNER,
            new JoinCondition(deptNoFromDept, deptNoFromDeptEmp,
                JoinCondition.JoinOperator.EQUAL)
        );

        SqlCommand command = joinDataSet.toSqlCommand();

        assertEquals("SELECT * FROM DEPARTMENTS d INNER JOIN DEPT_EMP e ON d.dept_no = e.dept_no",
            command.getStatement());
    }

    @Test
    public void testJoinDemonstratingColumnDataSetTraceability() {
        TableDataSet departments = new TableDataSet("DEPARTMENTS");
        TableDataSet deptEmp = new TableDataSet("DEPT_EMP");

        Column deptNoFromDept = departments.col("dept_no");
        Column deptNoFromDeptEmp = deptEmp.col("dept_no");

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

        Column deptName = departments.col("dept_name");
        Column empName = deptEmp.col("emp_name");

        DataSet selected = joinDataSet.select(deptName, empName);
        SqlCommand selectCommand = selected.toSqlCommand();

        assertEquals("SELECT DEPARTMENTS.dept_name, DEPT_EMP.emp_name FROM DEPARTMENTS INNER JOIN DEPT_EMP ON DEPARTMENTS.dept_no = DEPT_EMP.dept_no",
            selectCommand.getStatement());
    }
}
