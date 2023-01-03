package com.hyd.hybatis.sql;

import com.hyd.hybatis.row.Row;

import java.util.*;
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
            row -> {
                List<Object> paramValues = new ArrayList<>();
                for (String columnName : columnNameList) {
                    paramValues.add(row.get(columnName));
                }
                return paramValues;
            }
        );

        return new BatchCommand(insertSql, batchParams);
    }
}
