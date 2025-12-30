package com.hyd.hybatis.query.filter;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Filter;
import com.hyd.hybatis.sql.SqlCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.hyd.hybatis.utils.Obj.isEmpty;
import static java.util.Collections.emptyList;

@Getter
@Setter
public class CompositeFilter implements Filter {

    public enum Operator {
        AND, OR, NOT
    }

    private List<Filter> filters;

    private Operator operator;

    @Override
    public Column getColumn() {
        // 组合过滤条件没有单一字段
        return null;
    }

    @Override
    public List<Object> getValues() {
        return null;
    }

    @Override
    public SqlCommand toSqlFragment() {
        var statements = new ArrayList<String>();
        var params = new ArrayList<>();
        for (var match : filters) {
            if (match == null) {
                continue;
            }
            var sql = match.toSqlFragment();
            if (sql == null || isEmpty(sql.getStatement())) {
                continue;
            }
            statements.add(sql.getStatement());
            params.addAll(sql.getParams());
        }

        if (statements.isEmpty()) {
            return new SqlCommand("", emptyList());
        } else if (operator == Operator.NOT) {
            // NOT 是前缀操作符，只需要处理第一个匹配项
            if (filters.get(0) instanceof Exists) {
                return new SqlCommand("NOT " + statements.get(0), params);
            }
            return new SqlCommand("NOT (" + statements.get(0) + ")", params);
        } else if (statements.size() == 1) {
            return new SqlCommand(statements.get(0), params);
        } else {
            return new SqlCommand("(" + String.join(" " + operator.name() + " ", statements) + ")", params);
        }
    }
}
