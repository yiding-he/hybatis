package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbColumn;
import lombok.Data;

@Data
public class Employee extends BaseEntity {

    private Integer employeeId;

    private String firstName;

    private String lastName;

    private String title;

    private String email;

    private String address;
}
