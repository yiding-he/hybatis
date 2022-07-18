package com.hyd.hybatis;

import com.hyd.hybatis.annotations.HbArgument;
import com.hyd.hybatis.entity.User;
import com.hyd.hybatis.entity.UserCteQuery;
import com.hyd.hybatis.entity.UserQuery;
import com.hyd.hybatis.entity.UserUpdate;
import com.hyd.hybatis.mappers.UserMapper;
import com.hyd.hybatis.sql.Sql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(HybatisConfigurator.class)
@EnableConfigurationProperties(HybatisConfiguration.class)
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

    @Autowired
    private Hybatis hybatis;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            userMapper.createUserTable();
            userMapper.insertUser(1L, "Zhang San");
            userMapper.insertUser(2L, "Li Si");
            userMapper.insertUser(3L, "Wang wu");
            userMapper.insertUser(4L, null);
            userMapper.insertUserObject(new User(5L, "Zhao liu"));
            hybatis.execute(Sql.Insert("users").Values("user_id", 6L).Values("user_name", "Alice Baby"));
            log.info("Users created.");

            // Conditions demonstration
            Conditions conditions = new Conditions()
                .with("user_id", c -> c.between(1, 10))
                .with("user_name", c -> c.contains("a"));
            List<User> users = userMapper.selectByConditions(conditions);
            log.info("Users selected by conditions: {}", users);

            // Condition demonstration
            var users1 = userMapper.selectByCondition(Condition.of("user_id").eq(1));
            log.info("Users selected by condition: {}", users1);
        };
    }

    @RestController
    @RequestMapping("/users")
    public static class UserController {

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private Hybatis hybatis;

        // curl "http://localhost:8080/users/query?id.eq=1"
        // curl "http://localhost:8080/users/query?id.gt=1&id.lt=4"
        // curl "http://localhost:8080/users/query?userName.null=true"
        // curl "http://localhost:8080/users/query?id.orderAsc=1&userName.orderDesc=2"
        // curl "http://localhost:8080/users/query?id.eq=1"
        // curl "http://localhost:8080/users/query?id.eq=1"
        @GetMapping("/query")
        public List<User> getUsers(@HbArgument UserQuery userQuery) {
            return userMapper.selectByQuery(userQuery);
        }

        @GetMapping("/query-cte")
        public List<User> getUsersCte(@HbArgument UserCteQuery userQuery) {
            return userMapper.selectByQueryCte(userQuery);
        }

        @GetMapping("/queryMap")
        public List<Map<String, Object>> getUsersMap(@HbArgument UserQuery userQuery) {
            return userMapper.selectMapByQuery(userQuery);
        }

        // curl "http://localhost:8080/users/query-conditions"
        @GetMapping("/query-conditions")
        public List<User> queryByConditions(@HbArgument Conditions conditions) {
            return userMapper.selectByConditions(conditions);
        }

        // curl "http://localhost:8080/users/insert?id=6&name=John"
        @GetMapping("/insert")
        public String insertUser(
            @RequestParam("id") Long id,
            @RequestParam(value = "name", required = false) String name
        ) throws Exception {
            var affected = hybatis.execute(Sql.Insert("users")
                .Values("user_id", id)
                .Values("user_name", name)
            );
            return "OK, " + affected + " rows affected.";
        }

        // curl "http://localhost:8080/users/insert-obj?userId=6&userName=John"
        @GetMapping("/insert-obj")
        public String insertUser(User user) throws Exception {
            var affected = userMapper.insertUserObject(user);
            return "OK, " + affected + " rows affected.";
        }

        @GetMapping("/update")
        public String updateUser(UserUpdate userUpdate) {
            userMapper.updateUser(userUpdate.getQuery(), userUpdate.getUpdate());
            return "OK";
        }

        // POST body: {"query":{"id":{"eq":1}},"update":{"userName":"Hehehehehehe"}}
        @PostMapping("/update-json")
        public String updateUserJson(@RequestBody UserUpdate userUpdate) {
            userMapper.updateUser(userUpdate.getQuery(), userUpdate.getUpdate());
            return "OK";
        }
    }

}
