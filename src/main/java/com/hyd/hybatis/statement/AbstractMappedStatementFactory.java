package com.hyd.hybatis.statement;

import com.hyd.hybatis.driver.HybatisLanguageDriver;
import org.apache.ibatis.scripting.LanguageDriver;

public abstract class AbstractMappedStatementFactory implements MappedStatementFactory {

    private LanguageDriver languageDriver = new HybatisLanguageDriver();

    public LanguageDriver getLanguageDriver() {
        return languageDriver;
    }

    public void setLanguageDriver(LanguageDriver languageDriver) {
        this.languageDriver = languageDriver;
    }
}
