package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbPageQuery;
import lombok.Data;

import java.util.Date;

@Data
@HbPageQuery
public class EmployeeQuery {

    private Condition<Integer> empNo;

    private Condition<Date> birthDate;

    private Condition<String> firstName;

    private Condition<String> lastName;

    private Condition<String> gender;

    private Condition<Date> hireDate;

}
