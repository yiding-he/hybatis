package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;

import java.util.List;

/**
 * Mapper with predefined CRUD methods.
 *
 * @param <T> Entity type, should be annotated with {@link com.hyd.hybatis.annotations.HbEntity}
 *
 * @deprecated This interface is still in development.
 */
public interface CrudMapper<T> {

    int insert(T insertEntity);

    int update(Conditions conditions, T updateEntity);

    int delete(Conditions conditions);

    List<T> selectList(Conditions conditions);

    T selectOne(Conditions conditions);

}
