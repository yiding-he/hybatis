package com.hyd.hybatis.entity;

import com.hyd.hybatis.annotations.HbEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@HbEntity(table = "users")
public class User extends BaseEntity {

    private Long userId;

    private String userName;
}
