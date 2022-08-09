package com.hyd.hybatis.sql;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.page.Pagination;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

@Slf4j
public class SqlSourceForSelect extends HybatisSqlSource {

    private boolean counting;

    public SqlSourceForSelect(
        String sqlId, HybatisCore core, Configuration configuration, String tableName
    ) {
        super(sqlId, core, configuration, tableName);
    }

    public void setCounting(boolean counting) {
        this.counting = counting;
    }

    public boolean isCounting() {
        return counting;
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

        if (counting) {
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
        }

        log.info("[{}]: {}", getSqlId(), select.toCommand());
        return buildBoundSql(select);
    }

    /**
     * 创建一个同样查询条件，但只返回记录数的 SqlSource 对象
     */
    public SqlSourceForSelect newCountingSqlSource() {
        var clone = new SqlSourceForSelect(
            this.getSqlId(), this.getCore(), this.getConfiguration(), this.getTableName()
        );
        clone.setCounting(true);
        return clone;
    }
}
