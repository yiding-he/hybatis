package com.hyd.hybatis.mybatis;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.entity.Department;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResultSetHandlerTest extends HybatisSpringBootTestApplicationTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void test0() throws Exception {
        var entityClass = Department.class;
        var configuration = sqlSessionFactory.getConfiguration();
        var environment = configuration.getEnvironment();
        var dataSource = environment.getDataSource();
        var connection = dataSource.getConnection();

        var sql = "select * from DEPARTMENTS where DEPT_NO=?";
        var statement = connection.prepareStatement(sql);
        statement.setObject(1, "d001");
        statement.execute();

        var tmpId = UUID.randomUUID().toString().replace("-", "");
        var sqlSource = new SqlSourceBuilder(configuration).parse(null, null, Collections.emptyMap());
        var resultMap = new ResultMap.Builder(configuration, tmpId, entityClass, emptyList(), true).build();
        var mappedStatement = new MappedStatement
            .Builder(configuration, tmpId, sqlSource, SqlCommandType.SELECT)
            .resultMaps(List.of(resultMap))
            .build();

        var resultSetHandler = configuration.newResultSetHandler(
            null, mappedStatement, RowBounds.DEFAULT, null, null, null
        );
        var departments = resultSetHandler.<Department>handleResultSets(statement);

        assertNotNull(departments);
        assertFalse(departments.isEmpty());
        System.out.println(departments.get(0));
    }
}
