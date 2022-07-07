package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import lombok.Data;

@Data
public class UserCteQuery {

    private Condition<String> userName;

    private Condition<Long> userId;

}
