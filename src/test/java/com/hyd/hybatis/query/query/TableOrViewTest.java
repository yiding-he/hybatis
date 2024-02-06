package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Projection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hyd.hybatis.query.Aggregate.count;
import static com.hyd.hybatis.query.Match.equal;
import static com.hyd.hybatis.query.Projection.col;
import static com.hyd.hybatis.query.Projection.from;
import static org.junit.jupiter.api.Assertions.*;

class TableOrViewTest {

    @Test
    void test() {
        TableOrView userQuery = new TableOrView("user")
            .matches(List.of(
                equal("parent_id", 3),
                equal("id", 4)
            )).aggregates(List.of(
                count("privilege").as("privilege_count")
            )).projections(List.of(
                col("id").as("user_id"),
                from("role").col("id").as("role_id")
            ));

        System.out.println(userQuery.toSqlCommand());

        try {
            userQuery.validate();
            fail("此处的 SQL 应不合法");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
