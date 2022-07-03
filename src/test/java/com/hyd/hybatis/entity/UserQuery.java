package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.annotations.HbQuery;
import lombok.Data;

@Data
@HbQuery(table = "users")
public class UserQuery {

    private Condition<String> userName;

    private Condition<Long> userId;

    public Condition<String> userName() {
        return this.userName = this.userName == null ? new Condition<>() : this.userName;
    }

    public Condition<Long> userId() {
        return this.userId = this.userId == null ? new Condition<>() : this.userId;
    }
}
