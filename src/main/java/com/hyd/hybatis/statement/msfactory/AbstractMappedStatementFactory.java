package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.statement.MappedStatementFactory;
import org.apache.ibatis.scripting.LanguageDriver;

import java.lang.reflect.Method;

public abstract class AbstractMappedStatementFactory implements MappedStatementFactory {

    private LanguageDriver languageDriver = new HybatisLanguageDriver();

    private HybatisCore core;

    public HybatisConfiguration getHybatisConfiguration() {
        return core.getConf();
    }

    public void setCore(HybatisCore core) {
        this.core = core;
    }

    public LanguageDriver getLanguageDriver() {
        return languageDriver;
    }

    public void setLanguageDriver(LanguageDriver languageDriver) {
        this.languageDriver = languageDriver;
    }

    protected HybatisCore getCore() {
        return core;
    }

    protected String getTableName(Method method) {
        String tableName;

        if (method.isAnnotationPresent(HbSelect.class)) {
            tableName = method.getAnnotation(HbSelect.class).table();
        } else if (method.isAnnotationPresent(HbInsert.class)) {
            tableName = method.getAnnotation(HbInsert.class).table();
        } else if (method.isAnnotationPresent(HbUpdate.class)) {
            tableName = method.getAnnotation(HbUpdate.class).table();
        } else {
            throw new IllegalArgumentException("Method " + method.getName() + " contains no table information.");
        }

        var isSubQuery = tableName.length() > 7 && tableName.substring(0, 7).equalsIgnoreCase("select ");

        if (isSubQuery && method.isAnnotationPresent(HbUpdate.class)) {
            throw new IllegalArgumentException("Update method does not support sub query.");
        } else if (isSubQuery && method.isAnnotationPresent(HbInsert.class)) {
            throw new IllegalArgumentException("Insert method does not support sub query.");
        } else if (isSubQuery) {
            tableName = "(" + tableName + ") _hybatis_table_wrapper_";
        }

        return tableName;
    }
}
