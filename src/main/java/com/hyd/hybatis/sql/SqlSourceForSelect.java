package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.page.Pagination;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    /**
     * 查询分页模式，非分页查询则为 None，否则为其他两种模式之一
     */
    protected final SelectMode selectMode;

    /**
     * 拦截器自身无法得知执行的是哪个 Mapper 方法，需要在这里记下来
     */
    protected final Method mapperMethod;

    public SqlSourceForSelect(
        String sqlId, HybatisCore core, Configuration configuration, String tableName,
        SelectMode selectMode, Method mapperMethod
    ) {
        super(sqlId, core, configuration, tableName);
        this.selectMode = selectMode;
        this.mapperMethod = mapperMethod;
    }

    public Method getMapperMethod() {
        return mapperMethod;
    }

    @Override
    protected BoundSql build(Object parameterObject) {

        var context = new SqlHelper.Context(
            parameterObject, getTableName(), getHybatisConfiguration());

        Sql.Select select;
        if (parameterObject instanceof Conditions) {
            select = SqlHelper.buildSelectFromConditions(context);
        } else if (parameterObject instanceof Condition) {
            select = SqlHelper.buildSelectFromCondition(context);
        } else {
            select = SqlHelper.buildSelect(context);
        }

        if (selectMode == SelectMode.Count || selectMode == SelectMode.PaginationCount) {
            select.Columns("count(1)");
        } else {
            var fields = getFields();
            if (fields != null && fields.length > 0) {
                var columns = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    columns[i] = Str.camel2Underline(fields[i]);
                }
                select.Columns(columns);
            }

            if (selectMode == SelectMode.PaginationItems) {
                var pageIndex = Pagination.Context.getInstance().getPageIndex();
                var pageSize = Pagination.Context.getInstance().getPageSize();
                select.Offset((long) pageIndex * pageSize).Limit(pageSize);
            }
        }

        log.info("[{}]: {}", getSqlId(), select.toCommand());
        return buildBoundSql(select);
    }

    /**
     * 创建一个同样查询条件，但只返回记录数的 SqlSource 对象
     */
    public SqlSourceForSelect asAnotherSelectMode(SelectMode selectMode) {
        var newSqlId = this.sqlId + "-" + selectMode.ordinal();
        return new SqlSourceForSelect(
            newSqlId, this.core, this.configuration, this.tableName,
            selectMode, this.mapperMethod
        );
    }

    /**
     * 创建一个同样查询条件，但只返回记录数的 SqlSource 对象
     */
    public SqlSourceForSelect asPaginationCountSqlSource() {
        return asAnotherSelectMode(SelectMode.PaginationCount);
    }

    /**
     * 创建一个同样查询条件，但只返回当前页记录的 SqlSource 对象
     */
    public SqlSourceForSelect asPaginationItemsSqlSource() {
        return asAnotherSelectMode(SelectMode.PaginationItems);
    }
}
