package com.hyd.hybatis.query;

public interface Limit {

    int getLimit();

    int getOffset();

    default String appendLimit() {
        var statement = "";
        boolean hasLimit = getLimit() >= 0;
        boolean hasOffset = getOffset() > 0;

        if (hasOffset && !hasLimit) {
            throw new IllegalStateException("查询如果包含 offset，就必须指定 limit");
        }

        if (hasLimit) {
            statement += " LIMIT ";
            if (hasOffset) {
                statement += getOffset() + "," + getLimit();
            } else {
                statement += getLimit();
            }
        }
        return statement;
    }
}
