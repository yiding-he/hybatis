package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbColumn;
import com.hyd.hybatis.annotations.HbQuery;
import lombok.Data;

@Data
@HbQuery(table = "users")
public class UserQuery {

    private Condition<String> userName;

    @HbColumn("user_id")
    private Condition<Long> id;

}
