package com.hyd.hybatis.entity;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.row.Row;
import lombok.Data;

@Data
public class EmployeeUpdate {

    private Conditions query;

    private Row update;
}
