package com.hyd.hybatis;

import com.hyd.hybatis.entity.User;
import com.hyd.hybatis.mappers.UserMapper;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class MyBatisMapperTest {

    @Test
    public void test() throws Exception {
        var configuration = Initializer.initialize();
        configuration.addMapper(UserMapper.class);

        createMappedStatement(configuration);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.selectAll().forEach(System.out::println);
            userMapper.selectBySample(new User()).forEach(System.out::println);
        }
    }

    private void createMappedStatement(Configuration configuration) {
        var sqlId = UserMapper.class.getName() + ".selectBySample";
        var sql = "select * from users";
        var sqlSource = new SqlSourceBuilder(configuration).parse(sql, User.class, null);

        var ms = new MappedStatement.Builder(
            configuration, sqlId, sqlSource, SqlCommandType.SELECT
        ).resultMaps(Collections.singletonList(
            new ResultMap.Builder(configuration, sqlId + "_RM", User.class, Collections.emptyList(), true).build()
        )).build();
        configuration.addMappedStatement(ms);
    }
}
