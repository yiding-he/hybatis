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

    @HbSelect(table = "EMPLOYEES")
    Page<Employee> selectPageByQuery(EmployeeQuery query);

    @HbSelect(table = "EMPLOYEES")
    List<Employee> selectByQuery(EmployeeQuery query);

    @HbSelect(table = "EMPLOYEES")
    long countByConditions(Conditions conditions);

    @HbSelect(table = "EMPLOYEES")
    List<Row> selectByConditions(Conditions conditions);

    @HbSelect(table = "select * from EMPLOYEES where GENDER='F'")
    List<Employee> selectFemales(EmployeeQuery query);

    default List<Row> selectFemales() {
        return selectByConditions(new Conditions()
            .withColumn("GENDER").eq("F")
        );
    }

    @HbSelect(table = "EMPLOYEES")
    List<Row> selectRowsByQuery(EmployeeQuery query);

    /////////////////////////////////////////////////////////////////// Update

    @HbUpdate(table = "EMPLOYEES", key = "emp_no")
    void updateEmployee(Employee employee);

}
