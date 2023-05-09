package com.hyd.hybatis.mappers;

import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.pagination.PageCrudMapper;
import com.hyd.hybatis.row.Row;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@HbMapper(table = "EMPLOYEES", primaryKeyNames = "EMP_NO")
public interface EmployeeRowMapper extends PageCrudMapper<Row> {

}
