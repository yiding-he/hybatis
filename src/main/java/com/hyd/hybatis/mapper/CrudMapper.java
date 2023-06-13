package com.hyd.hybatis.mapper;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.annotations.HbDelete;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.utils.MapperUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper with predefined CRUD methods.
 * <p>
 * An empty table name in the annotation means that
 * the actual table name will be obtained from @HbEntity or @HbMapper.
 *
 * @param <T> Entity type, should be annotated with {@link com.hyd.hybatis.annotations.HbEntity}
 */
public interface CrudMapper<T> {

    /**
     * Default method for insertion.
     *
     * @param insertEntity entity to be inserted
     *
     * @return affected rows
     */
    @HbInsert(table = "")
    int insert(T insertEntity);

    /**
     * Default method for update.
     *
     * @param conditions   query conditions
     * @param updateEntity entity which contains updating properties
     *
     * @return number of rows affected
     */
    @HbUpdate(table = "")
    int update(Conditions conditions, T updateEntity);

    /**
     * Default method for query.
     *
     * @param conditions query conditions
     *
     * @return query result
     */
    @HbSelect(table = "")
    List<T> selectList(Conditions conditions);

    @HbSelect(table = "")
    Long count(Conditions conditions);

    @HbDelete(table = "")
    int delete(Conditions conditions);

    /**
     * Retrieve first query result.
     *
     * @param conditions query conditions
     *
     * @return first query result
     */
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

    /**
     * Retrieve entity by primary key.
     *
     * @param primaryKeyValues primary key values
     *
     * @return entity
     */
    default T findById(Object... primaryKeyValues) {
        return selectOne(findByIdConditions(primaryKeyValues));
    }

    /**
     * Update entity by primary key.
     *
     * @param primaryKeyValuesAndUpdate key and update values. The last argument is the update entity.
     */
    @SuppressWarnings("unchecked")
    default void updateById(Object... primaryKeyValuesAndUpdate) {
        if (primaryKeyValuesAndUpdate == null || primaryKeyValuesAndUpdate.length < 2) {
            throw new IllegalArgumentException("Insufficient count of argument.");
        }
        var primaryKeyValues = Arrays.copyOf(primaryKeyValuesAndUpdate, primaryKeyValuesAndUpdate.length - 1);
        var updateEntity = (T) primaryKeyValuesAndUpdate[primaryKeyValuesAndUpdate.length - 1];
        update(findByIdConditions(primaryKeyValues), updateEntity);
    }

    /**
     * Delete entity by primary key.
     *
     * @param primaryKeyValues primary key values.
     *
     * @return the number of rows deleted.
     */
    default int deleteById(Object... primaryKeyValues) {
        return delete(findByIdConditions(primaryKeyValues));
    }
}
