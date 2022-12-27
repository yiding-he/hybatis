package com.hyd.hybatis.mappers;

import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.pagination.PageCrudMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeCrudMapper extends PageCrudMapper<Employee> {

}
