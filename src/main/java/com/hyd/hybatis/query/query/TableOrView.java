package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.aggregate.AbstractAggregate;
import com.hyd.hybatis.query.projection.PlainProjection;
import com.hyd.hybatis.sql.SqlCommand;
import com.hyd.hybatis.utils.Obj;
import com.hyd.hybatis.utils.Str;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.hyd.hybatis.utils.Obj.defaultValue;
import static com.hyd.hybatis.utils.Obj.isNotEmpty;

/**
 * 直接从表或视图中进行查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableOrView extends AbstractQuery<TableOrView> {

    private String name;

    public TableOrView() {
    }

    public TableOrView(String name) {
        this.name = name;
    }

    @Override
    public String getAlias() {
        return Str.firstNonBlank(super.getAlias(), this.name);
    }

    private Projection fixProjection(Projection p) {
        if (p instanceof PlainProjection) {
            return Projection.from(getAlias()).col(p.getField()).as(p.getAlias());
        } else {
            return p;
        }
    }

    @Override
    public SqlCommand toSqlCommand() {

        // 选取字段补完表名，以免出现冲突
        this.setProjections(this.getProjections().stream().map(this::fixProjection).collect(Collectors.toList()));

        // 聚合字段补完表名，以免出现冲突
        this.getAggregates().forEach(a -> {
            if (a instanceof AbstractAggregate<?>) {
                var aa = (AbstractAggregate<?>) a;
                aa.setProjection(this.fixProjection(aa.getProjection()));
            }
        });

        var statement = "SELECT " + defaultValue(getProjectionsStatement(), "*") + " FROM " + getAlias();

        final var params = new ArrayList<>();
        if (isNotEmpty(this.getMatches())) {
            var matchSql = Match.AND(this.getMatches()).toSqlCommand();
            statement += " WHERE " + matchSql.getStatement();
            params.addAll(matchSql.getParams());
        }

        return new SqlCommand(statement, params);
    }

    @Override
    public void validate() {
        getProjections().stream().filter(p -> Obj.isNotEmpty(p.getFrom())).forEach(p -> {
            if (!p.getFrom().equals(getAlias())) {
                throw new IllegalArgumentException("Projection '" + p.toSqlExpression() +
                    "' cannot be selected from table or view '" + getAlias() + "'"
                );
            }
        });
    }
}
