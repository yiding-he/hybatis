package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbEntity;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.reflection.Reflections;

import java.util.List;

/**
 * Mapper with predefined CRUD methods.
 *
 * @param <T> Entity type, should be annotated with {@link com.hyd.hybatis.annotations.HbEntity}
 */
public interface CrudMapper<T> {

    @HbInsert(table = "")
    int insert(T insertEntity);

    @HbUpdate(table = "")
    int update(Conditions conditions, T updateEntity);

    @HbSelect(table = "")
    List<T> selectList(Conditions conditions);

    default T selectOne(Conditions conditions) {
        return selectList(conditions.limit(1)).stream().findFirst().orElse(null);
    }
}
