package com.hyd.hybatis.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BaseEntity {

    private Map<String, Object> params = new HashMap<>();
}
