package com.hyd.hybatis;

import com.hyd.hybatis.entity.UserQuery;
import com.hyd.hybatis.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(HybatisConfigurator.class)
public class HybatisSpringBootTestApplication {

    static {
        System.setProperty("spring.datasource.url", "jdbc:h2:mem:test");
        System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        System.setProperty("mybatis.configuration.map-underscore-to-camel-case", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(HybatisSpringBootTestApplication.class, args);
    }

    @Autowired
    private UserMapper userMapper;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            userMapper.createUserTable();
            userMapper.insertUser(1L, "Zhang San");
            userMapper.insertUser(2L, "Li Si");
            userMapper.insertUser(3L, "Wang wu");
            userMapper.insertUser(4L, null);

            UserQuery userQuery = new UserQuery();
            userQuery.userId().in(1L, 3L, 4L);

            userMapper.selectByQuery(userQuery).forEach(System.out::println);
        };
    }
}
