package com.hyd.hybatis.mappers;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.row.Row;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @HbSelect(table = "DEPARTMENTS")
    List<Row> selectList(Conditions conditions);

    @Select("select * from DEPARTMENTS where DEPT_NO=#{deptNo} limit 1")
    Department selectOneById(@Param("deptNo") String deptNo);
}
