package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Column;
import com.hyd.hybatis.query.Match;
import com.hyd.hybatis.sql.SqlCommand;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import java.util.ArrayList;
import java.util.List;

public class CaseColumn extends AbstractColumn<CaseColumn> {

    private List<KeyValue<Match, Column<?>>> cases = new ArrayList<>();

    private Column<?> defaultColumn;

    public CaseColumn when(Match match, Column<?> column) {
        this.cases.add(new DefaultKeyValue<>(match, column));
        return this;
    }

    public CaseColumn otherwise(Column<?> column) {
        this.defaultColumn = column;
        return this;
    }

    @Override
    public SqlCommand getSqlCommand() {
        var command = new SqlCommand();
        command.append("CASE ");
        for (KeyValue<Match, Column<?>> caseItem : cases) {
            command.append("WHEN ");
            command.append(caseItem.getKey().toSqlFragment());
            command.append(" THEN ");
            command.append(caseItem.getValue().toSqlFragment());
        }
        if (defaultColumn!= null) {
            command.append(" ELSE ");
            command.append(defaultColumn.toSqlFragment());
        }
        command.append(" END");
        return command;
    }
}
