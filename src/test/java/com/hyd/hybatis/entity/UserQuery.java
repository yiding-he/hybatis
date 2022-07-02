package com.hyd.hybatis.entity;

import com.hyd.hybatis.Condition;
import lombok.Data;

@Data
public class UserQuery {

    private Condition<String> userName;

    public Condition<String> userName() {
        return this.userName = this.userName == null ? new Condition<>() : this.userName;
    }

    private Condition<Long> userId;

    public Condition<Long> userId() {
        return this.userId = this.userId == null ? new Condition<>() : this.userId;
    }
}
