package com.hyd.hybatis.sql;

import com.hyd.hybatis.row.Row;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchCommandHelper {

    public static BatchCommand buildBatchInsertRows(String tableName, Collection<Row> rows) {
        Set<String> columns = new HashSet<>();
        rows.forEach(row -> columns.addAll(row.keySet()));

        var columnNameList = new ArrayList<>(columns);
        var qMarks = IntStream.range(0, columns.size()).mapToObj(__ -> "?").collect(Collectors.joining(","));

        var insertSql = "insert into " + tableName +
            "(" + String.join(",", columnNameList) + ") values (" + qMarks + ")";

        var batchParams = rows.stream().map(
            row -> columnNameList.stream().map(row::get).collect(Collectors.toList())
        );

        return new BatchCommand(insertSql, batchParams);
    }
}
