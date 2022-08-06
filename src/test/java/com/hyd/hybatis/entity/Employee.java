package com.hyd.hybatis.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Employee extends BaseEntity {

    private Integer empNo;

    private Date birthDate;

    private String firstName;

    private String lastName;

    private String gender;

    private Date hireDate;

}
