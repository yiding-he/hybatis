package com.hyd.hybatis;

import com.hyd.hybatis.entity.User;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class MyBatisBaseTest {

    @Test
    public void testBasicDatabaseOperation() throws Exception {
        Configuration configuration = MyBatisConfigurationBuilder.build();

        //////////////////////////

        SqlCommandType commandType = SqlCommandType.SELECT;
        String commandId = "sql1";
        String commandStatement = "select * from users where user_id=#{id}";

        String parameterName = "id";
        Class<?> parameterType = Integer.class;
        Object parameterValue = 1;

        Class<?> resultType = User.class;

        //////////////////////////

        // 1. 构建 SqlSource 对象
        SqlSource sqlSource = new SqlSourceBuilder(configuration).parse(commandStatement, parameterType, null);

        // 2. 构建 ResultMap 对象
        String resultMapId = commandId + "-Inline";
        ResultMap resultMap = new ResultMap.Builder(
            configuration, resultMapId, resultType, Collections.emptyList(), true
        ).build();

        // 3. 构建 ParameterMap 对象
        ParameterMap parameterMap = new ParameterMap.Builder(
            configuration, commandId, null,
            Collections.singletonList(
                new ParameterMapping.Builder(
                    configuration, parameterName, Integer.class
                ).build()
            )
        ).build();

        // 4. 将 SqlSource 对象、 ResultMap 对象和 ParameterMap 对象
        //    组合成最终的 MappedStatement 对象
        MappedStatement mappedStatement = new MappedStatement.Builder(
            configuration, commandId, sqlSource, commandType
        ).resultMaps(
            Collections.singletonList(resultMap)
        ).parameterMap(
            parameterMap
        ).build();

        //////////////////////////

        configuration.addMappedStatement(mappedStatement);

        //////////////////////////

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            List<Object> result = sqlSession.selectList(commandId, parameterValue);
            System.out.println(result);
        }
    }
}
