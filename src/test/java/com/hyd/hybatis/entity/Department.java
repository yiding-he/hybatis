package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbEntity;
import lombok.Data;

@Data
@HbEntity(table = "DEPARTMENTS", primaryKeyNames = "dept_no")
public class Department {

    private String deptNo;

    private String deptName;
}
