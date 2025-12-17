package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbEntity;
import lombok.Data;

@Data
@HbEntity(table = "DEPT_EMP", primaryKeyNames = {"dept_no", "emp_no"})
public class DeptEmp {

    private String deptNo;

    private Integer empNo;
}
