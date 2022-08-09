package com.hyd.hybatis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.entity.EmployeeQuery;
import com.hyd.hybatis.mappers.DepartmentMapper;
import com.hyd.hybatis.mappers.EmployeeMapper;
import com.hyd.hybatis.row.Row;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@SpringBootApplication
@Import(HybatisConfigurator.class)
@EnableConfigurationProperties(HybatisConfiguration.class)
@Slf4j
public class HybatisSpringBootTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HybatisSpringBootTestApplication.class, args);
    }

    @RestController
    @RequestMapping("/dep")
    public static class DepartmentController {

        @Autowired
        private DepartmentMapper departmentMapper;

        @GetMapping("/query")
        public List<Row> queryDepartments(Conditions conditions) {
            return departmentMapper.selectList(conditions);
        }
    }

    @RestController
    @RequestMapping("/emp")
    public static class EmployeeController {

        @Autowired
        private EmployeeMapper employeeMapper;

        @Autowired
        private HttpServletRequest request;

        /**
         * Integrate with <a href="https://github.com/pagehelper/Mybatis-PageHelper">Mybatis-PageHelper</a>
         */
        @SuppressWarnings("resource")
        private void startPage() {
            if (!hasText(request.getParameter("pageIndex")) || !hasText(request.getParameter("pageSize"))) {
                PageHelper.startPage(1, 10);
            } else {
                PageHelper.startPage(
                    Integer.parseInt(request.getParameter("pageIndex")),
                    Integer.parseInt(request.getParameter("pageSize"))
                );
            }
        }

        @Data
        private static class RealPage {

            private final Page<?> list;

            private final int total;

            private final int pages;
        }

        // curl "http://localhost:8080/emp/query?firstName.eq=Bikash"
        @GetMapping("/query")
        public RealPage queryEmployees(EmployeeQuery employeeQuery) {
            startPage();
            var page = employeeMapper.selectByQuery(employeeQuery);
            return new RealPage(page, (int) page.getTotal(), page.getPages());
        }

        // curl "http://localhost:8080/emp/count?hire_date.gt=1994-12-31"
        @GetMapping("/count")
        public long countEmployees(Conditions conditions) {
            return employeeMapper.countByConditions(conditions);
        }

        @PostMapping(value = "/update", consumes = "application/json")
        public String updateEmployee(@RequestBody Employee update) {
            employeeMapper.updateEmployee(update);
            return "ok";
        }
    }

}
