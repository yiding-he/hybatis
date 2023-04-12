package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbDelete;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;

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

    @HbSelect(table = "")
    Long count(Conditions conditions);

    @HbDelete(table = "")
    int delete(Conditions conditions);

    default T selectOne(Conditions conditions) {
        return selectList(conditions.limit(1)).stream().findFirst().orElse(null);
    }
}
