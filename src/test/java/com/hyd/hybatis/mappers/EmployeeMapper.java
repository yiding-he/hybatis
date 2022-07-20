package com.hyd.hybatis.mappers;

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

    @HbSelect(table = "employee")
    List<Employee> selectByQuery(EmployeeQuery query);

    @HbSelect(table = "select employeeid,firstname,lastname from employee")
    List<Employee> selectByQueryCte(EmployeeQuery query);

    @HbSelect(table = "employee")
    List<Row> selectRowsByQuery(EmployeeQuery query);

    @HbSelect(table = "employee")
    List<Employee> selectByConditions(Conditions conditions);

    @HbUpdate(table = "employee")
    void updateEmployee(Conditions query, Row update);
}
