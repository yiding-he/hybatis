package com.hyd.hybatis.mappers;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.row.Row;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @HbSelect(table = "DEPARTMENTS")
    List<Row> selectList(Conditions conditions);

}
