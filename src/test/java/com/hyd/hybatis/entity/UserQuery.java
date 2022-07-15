package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbColumn;
import lombok.Data;

@Data
public class UserQuery {

    private Condition<String> userName = new Condition<>();

    @HbColumn("user_id")
    private Condition<Long> id = new Condition<>();

}
