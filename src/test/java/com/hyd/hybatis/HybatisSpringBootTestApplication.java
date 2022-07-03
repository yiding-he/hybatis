package com.hyd.hybatis;

import com.hyd.hybatis.entity.User;
import com.hyd.hybatis.entity.UserCteQuery;
import com.hyd.hybatis.entity.UserQuery;
import com.hyd.hybatis.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(HybatisConfigurator.class)
@Slf4j
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
            log.info("Users created.");
        };
    }

    @RestController
    @RequestMapping("/users")
    public static class UserController {

        @Autowired
        private UserMapper userMapper;

        // curl "http://localhost:8080/users/query?id.eq=1"
        // curl "http://localhost:8080/users/query?id.gt=1&id.lt=4"
        // curl "http://localhost:8080/users/query?userName.null=true"
        // curl "http://localhost:8080/users/query?id.orderAsc=1&userName.orderDesc=2"
        // curl "http://localhost:8080/users/query?id.eq=1"
        // curl "http://localhost:8080/users/query?id.eq=1"
        @GetMapping("/query")
        public List<User> getUsers(UserQuery userQuery) {
            return userMapper.selectByQuery(userQuery);
        }

        @GetMapping("/query-cte")
        public List<User> getUsersCte(UserCteQuery userQuery) {
            return userMapper.selectByQueryCte(userQuery);
        }

        @GetMapping("/queryMap")
        public List<Map<String, Object>> getUsersMap(UserQuery userQuery) {
            return userMapper.selectMapByQuery(userQuery);
        }
    }

}
