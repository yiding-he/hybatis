package com.hyd.hybatis.query.query;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hyd.hybatis.query.Aggregate.count;
import static com.hyd.hybatis.query.Match.equal;
import static com.hyd.hybatis.query.Projection.*;

class TableOrViewTest {

    @Test
    void test() {
        TableOrView u = new TableOrView("user").as("u");
        u = u.matches(List.of(
            equal(u.col("parent_id"), 3),
            equal(u.col("id"), 4)
        )).aggregates(List.of(
            count(u.col("privilege")).as("privilege_count")
        )).projections(List.of(
            col("id").as("user_id"),
            from(u).col("name").as("user_name"),
            plain("'true'").as("is_valid")
        ));

        System.out.println(u.toSqlCommand());
        u.validate();
    }
}
