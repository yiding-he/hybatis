package com.hyd.hybatis.sql.dialect;

public class DefaultDialect implements Dialect {

    @Override
    public String nowFunction() {
        return "NOW()";
    }
}
