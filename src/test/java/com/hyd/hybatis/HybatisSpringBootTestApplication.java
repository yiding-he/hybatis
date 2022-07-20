package com.hyd.hybatis;

import com.hyd.hybatis.annotations.HbArgument;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.entity.EmployeeQuery;
import com.hyd.hybatis.entity.EmployeeUpdate;
import com.hyd.hybatis.mappers.EmployeeMapper;
import com.hyd.hybatis.row.Row;
import com.hyd.hybatis.sql.Sql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(HybatisConfigurator.class)
@EnableConfigurationProperties(HybatisConfiguration.class)
@Slf4j
public class HybatisSpringBootTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HybatisSpringBootTestApplication.class, args);
    }

    @RestController
    @RequestMapping("/emp")
    public static class EmployeeController {

        @Autowired
        private EmployeeMapper employeeMapper;

        @Autowired
        private Hybatis hybatis;

        // curl "http://localhost:8080/emp/query?id.eq=1"
        @GetMapping("/query")
        public List<Employee> getUsers(@HbArgument EmployeeQuery employeeQuery) {
            return employeeMapper.selectByQuery(employeeQuery);
        }

        @GetMapping("/query-cte")
        public List<Employee> getUsersCte(@HbArgument EmployeeQuery employeeQuery) {
            return employeeMapper.selectByQueryCte(employeeQuery);
        }

        @GetMapping("/queryMap")
        public List<Row> getUsersMap(@HbArgument EmployeeQuery employeeQuery) {
            return employeeMapper.selectRowsByQuery(employeeQuery);
        }

        // curl "http://localhost:8080/users/query-conditions"
        @GetMapping("/query-conditions")
        public List<Employee> queryByConditions(@HbArgument Conditions conditions) {
            return employeeMapper.selectByConditions(conditions);
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


        // POST body: {"query":{"id":{"eq":1}},"update":{"userName":"Hehehehehehe"}}
        @PostMapping("/update-json")
        public String updateUserJson(@RequestBody EmployeeUpdate update) {
            employeeMapper.updateEmployee(update.getQuery(), update.getUpdate());
            return "OK";
        }
    }

}
