package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import lombok.Data;

@Data
public class UserCteQuery {

    private Condition<String> userName;

    private Condition<Long> userId;

}
