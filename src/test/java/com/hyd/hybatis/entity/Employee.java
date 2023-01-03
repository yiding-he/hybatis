package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@HbEntity(table = "EMPLOYEES")
public class Employee extends BaseEntity {

    private Integer empNo;

    private Date birthDate;

    private String firstName;

    private String lastName;

    private String gender;

    private Date hireDate;

}
