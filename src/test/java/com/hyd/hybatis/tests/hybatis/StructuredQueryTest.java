package com.hyd.hybatis.tests.hybatis;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.sql.Sql;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class StructuredQueryTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testDepartmentQuery() throws Exception {
        var cteDepartmentWithEmployeeCount =
            Sql.Select(
                    "dp.dept_no", "dp.dept_name", "COUNT(de.emp_no) AS employee_count"
                )
                .From("departments AS dp")
                .LeftJoin("dept_emp as de on de.dept_no=dp.dept_no")
                .GroupBy("dp.dept_no", "dp.dept_name")
                .AsCTE("cte_dpe");

        var cteDepartmentWithManagerName =
            Sql.Select(
                    "dp.dept_no",
                    "e.first_name as manager_first_name",
                    "e.last_name as manager_last_name"
                )
                .From("departments as dp")
                .LeftJoin("dept_manager dm on dm.dept_no=dp.dept_no")
                .LeftJoin("employees e on e.emp_no=dm.emp_no")
                .AsCTE("cte_dpm");

        var query =
            Sql.Select(
                    "cte_dpe.*", "cte_dpm.manager_first_name", "cte_dpm.manager_last_name"
                )
                .From("cte_dpe")
                .LeftJoin("cte_dpm on cte_dpm.dept_no=cte_dpe.dept_no")
                .Ctes(
                    cteDepartmentWithEmployeeCount,
                    cteDepartmentWithManagerName
                );

        hybatis.query(query, row -> {
            log.info(String.valueOf(row));
        });
    }
}
