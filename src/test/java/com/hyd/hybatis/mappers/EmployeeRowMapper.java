package com.hyd.hybatis.mappers;

import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.pagination.PageCrudMapper;
import com.hyd.hybatis.row.Row;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@HbMapper(table = "EMPLOYEES", primaryKeyNames = "EMP_NO")
public interface EmployeeRowMapper extends PageCrudMapper<Row> {

    @Select("select * from EMPLOYEES")
    List<Row> selectAllEmployees();

}
