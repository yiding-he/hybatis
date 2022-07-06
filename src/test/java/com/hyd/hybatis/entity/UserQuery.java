package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbColumn;
import com.hyd.hybatis.annotations.HbQuery;
import lombok.Data;
import lombok.experimental.Accessors;

@HbQuery(table = "users")
@Data
public class UserQuery {

    private Condition<String> userName = new Condition<>();

    @HbColumn("user_id")
    private Condition<Long> id = new Condition<>();

}
