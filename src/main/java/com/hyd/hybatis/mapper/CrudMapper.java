package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbDelete;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.utils.MapperUtil;

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

    default Conditions findByIdConditions(Object... primaryKeyValues) {
        var primaryKeyNames = MapperUtil.primaryKeyNames(getClass());
        if (primaryKeyNames == null || primaryKeyNames.length == 0) {
            throw new IllegalStateException("No primary keys defined");
        } else if (primaryKeyValues == null || primaryKeyValues.length < primaryKeyNames.length) {
            throw new IllegalArgumentException("Insufficient count of argument.");
        }

        var conditions = new Conditions();
        for (int i = 0; i < primaryKeyNames.length; i++) {
            String column = primaryKeyNames[i];
            conditions.withColumn(column).eq(primaryKeyValues[i]);
        }
        return conditions;
    }

    default T findById(Object... primaryKeyValues) {
        return selectOne(findByIdConditions(primaryKeyValues));
    }

    default int deleteById(Object... primaryKeyValues) {
        return delete(findByIdConditions(primaryKeyValues));
    }
}
