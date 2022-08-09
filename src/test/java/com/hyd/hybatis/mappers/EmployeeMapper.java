package com.hyd.hybatis.mappers;

import com.github.pagehelper.Page;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.entity.EmployeeQuery;
import com.hyd.hybatis.row.Row;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /////////////////////////////////////////////////////////////////// Query

    @HbSelect(table = "EMPLOYEES", fields = {"empNo", "firstName", "lastName", "hireDate"})
    Page<Employee> selectByQuery(EmployeeQuery query);

    @HbSelect(table = "EMPLOYEES")
    List<Employee> selectByConditions(Conditions conditions);

    @HbSelect(table = "EMPLOYEES")
    long countByConditions(Conditions conditions);

    @HbSelect(table = "select * from EMPLOYEES where GENDER='F'")
    List<Employee> selectFromFemales(EmployeeQuery query);

    @HbSelect(table = "EMPLOYEES")
    List<Row> selectRowsByQuery(EmployeeQuery query);

    /////////////////////////////////////////////////////////////////// Update

    @HbUpdate(table = "EMPLOYEES", key = "emp_no")
    void updateEmployee(Employee employee);

}
