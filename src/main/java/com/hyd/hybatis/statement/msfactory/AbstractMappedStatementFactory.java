package com.hyd.hybatis.statement.msfactory;

import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.HybatisCore;
import com.hyd.hybatis.annotations.HbDelete;
import com.hyd.hybatis.annotations.HbInsert;
import com.hyd.hybatis.annotations.HbSelect;
import com.hyd.hybatis.annotations.HbUpdate;
import com.hyd.hybatis.driver.HybatisLanguageDriver;
import com.hyd.hybatis.mapper.CrudMapper;
import com.hyd.hybatis.statement.MappedStatementFactory;
import com.hyd.hybatis.utils.MapperUtil;
import com.hyd.hybatis.utils.Result;
import com.hyd.hybatis.utils.Str;
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

    @SuppressWarnings("unchecked")
    public static Result<String> getTableName(Class<?> mapperClass, Method method) {
        String tableName = null;

        if (method.isAnnotationPresent(HbSelect.class)) {
            tableName = method.getAnnotation(HbSelect.class).table();
        } else if (method.isAnnotationPresent(HbInsert.class)) {
            tableName = method.getAnnotation(HbInsert.class).table();
        } else if (method.isAnnotationPresent(HbUpdate.class)) {
            tableName = method.getAnnotation(HbUpdate.class).table();
        } else if (method.isAnnotationPresent(HbDelete.class)) {
            tableName = method.getAnnotation(HbDelete.class).table();
        }

        if (Str.isBlank(tableName) && CrudMapper.class.isAssignableFrom(mapperClass)) {
            tableName = MapperUtil.getTableName(mapperClass);
        }

        if (Str.isBlank(tableName)) {
            return Result.fail("Method " + method + " contains no table information.");
        }

        var isSubQuery = tableName.length() > 7 && tableName.substring(0, 7).trim().equalsIgnoreCase("select");

        if (isSubQuery && method.isAnnotationPresent(HbUpdate.class)) {
            return Result.fail("Update method " + method + " does not support sub query.");
        } else if (isSubQuery && method.isAnnotationPresent(HbInsert.class)) {
            return Result.fail("Insert method " + method + " does not support sub query.");
        } else if (isSubQuery) {
            tableName = "(" + tableName + ") _hybatis_table_wrapper_";
        }

        return Result.success(tableName);
    }
}
