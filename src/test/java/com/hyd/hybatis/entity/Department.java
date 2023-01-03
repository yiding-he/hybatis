package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbEntity;
import lombok.Data;

@Data
@HbEntity(table = "DEPARTMENTS")
public class Department {

    private String deptNo;

    private String deptName;
}
