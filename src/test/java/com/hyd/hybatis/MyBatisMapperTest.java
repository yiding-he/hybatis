package com.hyd.hybatis;

import com.hyd.hybatis.entity.User;
import com.hyd.hybatis.mappers.UserMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;

public class MyBatisMapperTest {

    @Test
    public void test() throws Exception {
        var configuration = Initializer.initialize();
        configuration.addMapper(UserMapper.class);

        new Hybatis().process(configuration);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.selectAll().forEach(System.out::println);
            userMapper.selectBySample(new User()).forEach(System.out::println);
        }
    }
}
