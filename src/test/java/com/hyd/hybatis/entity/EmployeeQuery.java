package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbColumn;
import lombok.Data;

@Data
public class EmployeeQuery {

    @HbColumn("employeeid")
    private Condition<Integer> employeeId;

    @HbColumn("firstname")
    private Condition<String> firstName;

    @HbColumn("lastname")
    private Condition<String> lastName;

    private Condition<String> title;

    private Condition<String> email;

    private Condition<String> address;

}
