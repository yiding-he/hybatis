package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import lombok.Data;

@Data
@HbQuery(table = "select * from users where user_name is not null")
public class UserCteQuery {

    private Condition<String> userName;

    private Condition<Long> userId;

}
