package com.hyd.hybatis.query.query;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hyd.hybatis.query.Aggregate.count;
import static com.hyd.hybatis.query.Column.lit;
import static com.hyd.hybatis.query.Match.equal;

class WrapTest {


    @Test
    void test() {
        TableOrView u = new TableOrView("user").as("u");
        u = u.matches(List.of(
            equal(u.col("parent_id"), 3),
            equal(u.col("id"), 4)
        )).aggregates(List.of(
            count(u.col("privilege")).as("privilege_count")
        )).columns(List.of(
            u.col("id").as("user_id"),
            u.col("name").as("user_name"),
            lit("'true'").as("is_valid")
        ));

        Wrap w = new Wrap(u).as("w");
        w = w.matches(List.of(
            equal(w.col("id"), 1)
        )).columns(List.of(
            w.col("id").as("user_id"),
            w.col("name").as("user_name")
        ));

        System.out.println(w.toSqlCommand());
    }
}
