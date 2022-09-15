package com.hyd.hybatis;

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
import java.util.function.Supplier;

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
        @Data
        private static class PageHelperPage {

            @SuppressWarnings("resource")
            public PageHelperPage(HttpServletRequest request, Supplier<com.github.pagehelper.Page<?>> pageSupplier) {
                var pageNum = request.getParameter("pageIndex") == null ? 1 : Integer.parseInt(request.getParameter("pageIndex"));
                var pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
                PageHelper.startPage(pageNum, pageSize);
                var page = pageSupplier.get();
                this.list = page;
                this.total = (int) page.getTotal();
                this.pages = page.getPages();
            }

            private final com.github.pagehelper.Page<?> list;

            private final int total;

            private final int pages;
        }

        // curl "http://localhost:8080/emp/select-page-by-query?firstName.eq=Bikash"
        @GetMapping("/select-page-by-query")
        public PageHelperPage selectByQuery(EmployeeQuery employeeQuery) {
            return new PageHelperPage(request, () -> employeeMapper.selectByQuery(employeeQuery));
        }

        // curl "http://localhost:8080/emp/select-by-conditions?firstName.eq=Bikash&limit=4"
        @GetMapping("/select-by-conditions")
        public List<Row> selectByConditions(Conditions conditions) {
            conditions.limit(Math.min(conditions.getLimit(), 50));
            return employeeMapper.selectByConditions(conditions);
        }

        // curl "http://localhost:8080/emp/count?hire_date.gt=1994-12-31"
        @GetMapping("/count")
        public long countEmployees(Conditions conditions) {
            return employeeMapper.countByConditions(conditions);
        }

        // curl --request POST \
        //  --url http://localhost:8080/emp/update \
        //  --header 'Content-Type: application/json' \
        //  --data '{
        //	"empNo": 1,
        //	"firstName": "Hybatis",
        //	"lastName": "Smith"
        //}'
        @PostMapping("/update")
        public String updateEmployee(@RequestBody Employee update) {
            employeeMapper.updateEmployee(update);
            return "ok";
        }
    }

}
