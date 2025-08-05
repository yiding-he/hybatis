package com.hyd.hybatis.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试在插入和更新时排除指定类型（即 {@link BaseEntity}）中的所有成员
 */
@Data
public class BaseEntity {

    private Map<String, Object> params = new HashMap<>();
}
