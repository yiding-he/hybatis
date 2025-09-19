package com.hyd.hybatis.query;

import com.hyd.hybatis.Condition;
import lombok.Data;

/**
 * Query condition object used to restrict the query parameters,
 * preventing the query request from containing unauthorized conditions.
 */
@Data
public class EmployeeQuery {

    private Condition empNo;

    private Condition birthDate;

    private Condition firstName;

    private Condition lastName;

    private Condition gender;

    private Condition hireDate;

}
