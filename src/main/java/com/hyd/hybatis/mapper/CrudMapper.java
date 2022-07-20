package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;

import java.util.List;

public interface CrudMapper<T> {

    int insert(T insertEntity);

    int update(Conditions conditions, T updateEntity);

    int delete(Conditions conditions);

    List<T> selectList(Conditions conditions);

    T selectOne(Conditions conditions);

}
