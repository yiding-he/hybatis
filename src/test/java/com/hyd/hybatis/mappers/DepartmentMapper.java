package com.hyd.hybatis.mappers;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper extends CrudMapper<Department> {

    default List<Department> selectAll() {
        return selectList(new Conditions());
    }

    @HbInsert(table = "DEPARTMENTS", onDuplicateKeyIgnore = true)
    int insertIgnore(Department department);
}
