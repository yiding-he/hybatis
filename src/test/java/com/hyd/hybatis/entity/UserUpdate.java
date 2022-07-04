package com.hyd.hybatis.entity;

import lombok.Data;

@Data
public class UserUpdate {

    private UserQuery query;

    private User update;
}
