package com.hyd.hybatis;

import com.hyd.hybatis.mappers.UserMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;

public class MyBatisMapperTest {

    @Test
    public void test() throws Exception {

        var hybatisConf = new HybatisConfiguration();
        var hybatisCore = new HybatisCore(hybatisConf);

        var configuration = MyBatisConfigurationBuilder.build();
        configuration.addMapper(UserMapper.class);
        hybatisCore.process(configuration);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.selectAll().forEach(System.out::println);
        }
    }
}
