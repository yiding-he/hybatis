package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.Table;
import lombok.Data;

@Table("users")
@Data
public class User {

    private Long userId;

    private String userName;
}
