package com.hyd.hybatis;

import org.h2.tools.RunScript;

import java.sql.SQLException;

public class RunScriptMain {

    public static void main(String[] args) throws SQLException {
        RunScript.main("-url", "jdbc:h2:file:./target/data/test", "-script", "src/test/resources/northwind.sql");
    }
}
