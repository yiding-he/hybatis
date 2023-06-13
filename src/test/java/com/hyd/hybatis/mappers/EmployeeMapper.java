package com.hyd.hybatis.mappers;

import com.github.pagehelper.Page;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.query.EmployeeQuery;
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
    List<Row> selectRowsByQuery(EmployeeQuery query);

    @HbSelect(table = "EMPLOYEES")
    long countByConditions(Conditions conditions);

    @HbSelect(table = "EMPLOYEES")
    List<Row> selectRowsByConditions(Conditions conditions);

    @HbSelect(table = "EMPLOYEES")
    List<Employee> selectByConditions(Conditions conditions);

    default List<Employee> selectFemales() {
        return selectByConditions(new Conditions()
            .withColumn("GENDER").eq("F")
        );
    }

    default List<Row> selectFemaleRows() {
        return selectRowsByConditions(new Conditions()
            .withColumn("GENDER").eq("F")
        );
    }

    /////////////////////////////////////////////////////////////////// Update

    @HbUpdate(table = "EMPLOYEES")
    void updateEmployee(Conditions conditions, Employee employee);

}
