package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbTable;
import lombok.Data;

@HbTable("users")
@Data
public class User {

    private Long userId;

    private String userName;
}
