package com.hyd.hybatis.mappers;

import com.hyd.hybatis.annotations.HbMapper;
import com.hyd.hybatis.entity.NonAnnotatedDepartment;
import com.hyd.hybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@HbMapper(table = "DEPARTMENTS")
public interface NonAnnotatedDepartmentMapper extends CrudMapper<NonAnnotatedDepartment> {

}
