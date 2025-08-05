package com.hyd.hybatis.tests.hybatis;

import com.hyd.hybatis.HybatisSpringBootTestApplicationTest;
import com.hyd.hybatis.sql.BatchCommand;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HybatisBatchTest extends HybatisSpringBootTestApplicationTest {

    @Test
    public void testInsertBatch() throws Exception {
        hybatis.execute("drop table if exists test_batch");
        hybatis.execute("create table test_batch(id int, name varchar(20))");

        BatchCommand batchCommand = new BatchCommand();
        batchCommand.setStatement("insert into test_batch(id, name) values(?, ?)");

        List<List<Object>> params = new ArrayList<>();
        for (int i = 0; i < 345; i++) {
            params.add(List.of(i, String.valueOf(i)));
        }

        batchCommand.setParams(params.stream());
        var effectedRows = hybatis.execute(batchCommand);
        assertEquals(params.size(), effectedRows);

        System.out.println(hybatis.queryList("select count(1) as row_count from test_batch"));
    }
}
