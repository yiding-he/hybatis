package com.hyd.hybatis.query.query;

import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.EntityUtil;
import com.hyd.hybatis.utils.Str;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.util.Collections.emptyList;

/**
 * 以实体类来表示结构的数据集
 *
 * @param <T> 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Table<T> extends AbstractQuery<Table<T>> {

    public static <E> Table<E> of(Class<E> entityClass) {
        return new Table<>(entityClass);
    }

    private String name;

    private Class<T> entityClass;

    public Table(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.name = EntityUtil.getTableName(entityClass);
    }

    @Override
    public String getAlias() {
        return Str.firstNonBlank(super.getAlias(), this.name);
    }


    @Override
    public SqlCommand getFromFragment() {
        return new SqlCommand(getName(), emptyList());
    }
}
