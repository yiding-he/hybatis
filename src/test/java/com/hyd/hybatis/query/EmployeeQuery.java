package com.hyd.hybatis.query;

import com.hyd.hybatis.Condition;
import lombok.Data;

import java.util.Date;

/**
 * Query condition object used to restrict the query parameters,
 * preventing the query request from containing unauthorized conditions.
 */
@Data
public class EmployeeQuery {

    private Condition<Integer> empNo;

    private Condition<Date> birthDate;

    private Condition<String> firstName;

    private Condition<String> lastName;

    private Condition<String> gender;

    private Condition<Date> hireDate;

}
