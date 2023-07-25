package com.hyd.hybatis;

import com.hyd.hybatis.entity.Department;
import com.hyd.hybatis.entity.Employee;
import com.hyd.hybatis.mappers.DepartmentMapper;
import com.hyd.hybatis.mappers.EmployeeCrudMapper;
import com.hyd.hybatis.mappers.EmployeeMapper;
import com.hyd.hybatis.mappers.EmployeeRowMapper;
import com.hyd.hybatis.pagination.PageHelperPage;
import com.hyd.hybatis.query.EmployeeQuery;
import com.hyd.hybatis.row.Row;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.hyd.hybatis.Conditions.eq;

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

        // curl "http://localhost:8080/dep/query?deptNo.gt=d004"
        @GetMapping("/query")
        public List<Department> queryDepartments(Conditions conditions) {
            return departmentMapper.selectList(conditions);
        }
    }

    @RestController
    @Validated
    @RequestMapping("/emp")
    public static class EmployeeController {

        @Autowired
        private EmployeeMapper employeeMapper;

        @Autowired
        private EmployeeCrudMapper employeeCrudMapper;

        @Autowired
        private EmployeeRowMapper employeeRowMapper;

        @Autowired
        private HttpServletRequest request;

        // curl "http://localhost:8080/emp/select-page-by-query?firstName.eq=Bikash&pageNum=3&pageSize=5"
        // Mapper 返回 PageHelper 的 Page 对象，然后封装为 PageHelperPage
        @GetMapping("/select-page-by-query")
        public PageHelperPage<Employee> selectPageByQuery(@Valid EmployeeQuery employeeQuery) {
            return new PageHelperPage<>(request, () -> employeeMapper.selectPageByQuery(employeeQuery));
        }

        // curl "http://localhost:8080/emp/select-page-by-query-2?firstName.eq=Bikash&pageNum=3&pageSize=5"
        // Mapper 直接返回 PageHelperPage 对象
        @GetMapping("/select-page-by-query-2")
        public PageHelperPage<Employee> selectPageByQuery2(Conditions conditions) {
            return employeeCrudMapper.selectPage(conditions, request);
        }

        // curl "http://localhost:8080/emp/select-rows-page-by-conditions?firstName.eq=Bikash&pageNum=3&pageSize=5"
        @GetMapping("/select-rows-page-by-conditions")
        public PageHelperPage<Row> selectRowsPageByConditions(Conditions conditions) {
            conditions = conditions.projection("emp_no", "first_name", "last_name");
            return employeeRowMapper.selectPage(conditions, request);
        }

        // curl "http://localhost:8080/emp/select-rows-page-by-conditions?pageNum=3&pageSize=5"
        @GetMapping("/select-rows-page-by-conditions-2")
        public PageHelperPage<Row> selectRowsPageByConditions2() {
            return employeeRowMapper.selectPage(
                () -> employeeRowMapper.selectAllEmployees(), request
            );
        }

        // curl "http://localhost:8080/emp/select-by-conditions?firstName.eq=Bikash&limit=4&projection=empNo,firstName,lastName,hireDate"
        @GetMapping("/select-by-conditions")
        public List<Row> selectByConditions(Conditions conditions) {
            conditions.limit(Math.min(conditions.getLimit(), 50));
            return employeeMapper.selectRowsByConditions(conditions);
        }

        // curl "http://localhost:8080/emp/count?hire_date.gt=1994-12-31"
        @GetMapping("/count")
        public long countEmployees(Conditions conditions) {
            return employeeMapper.countByConditions(conditions);
        }

        // curl --request POST \
        //  --url http://localhost:8080/emp/update \
        //  --header 'Content-Type: application/json' \
        //  --data '{"empNo":1, "firstName":"Hybatis", "lastName":"Smith"}'
        @PostMapping("/update")
        public String updateEmployee(@RequestBody Employee update) {
            employeeMapper.updateEmployee(
                eq("emp_no", update.getEmpNo()), update
            );
            return "ok";
        }
    }

}
