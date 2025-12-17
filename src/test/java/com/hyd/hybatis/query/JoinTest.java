package com.hyd.hybatis.query;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.entity.DeptEmp;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.query.query.Table;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class JoinTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testJoin() throws SQLException {
        var employeeTable = Table.of(Employee.class).as("e");
        var departmentTable = Table.of(Department.class).as("d");
        var deptEmpTable = Table.of(DeptEmp.class).as("de");

        var query = employeeTable
            .join(join -> join.with(deptEmpTable).match(
                employeeTable.col(Employee::getEmpNo),
                deptEmpTable.col(DeptEmp::getEmpNo)
            ))
            .join(join -> join.with(departmentTable).match(
                deptEmpTable.col(DeptEmp::getDeptNo),
                departmentTable.col(Department::getDeptNo)
            ))
            .matches(
                Match.equal(employeeTable.col(Employee::getEmpNo), 10010)
            )
            .columns(
                employeeTable.col(Employee::getEmpNo),
                employeeTable.col(Employee::getFirstName),
                employeeTable.col(Employee::getLastName),
                departmentTable.col(Department::getDeptName)
            );

        hybatis.queryList(query.toSqlCommand()).forEach(System.out::println);
    }

}
