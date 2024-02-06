package com.hyd.hybatis.query.query;

import com.hyd.hybatis.query.Projection;
import com.hyd.hybatis.query.match.Equal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableOrViewTest {

    @Test
    void test() {
        TableOrView tableOrView = new TableOrView();
        tableOrView.setName("user");
        tableOrView.setMatches(List.of(
            new Equal("parent_id", 3),
            new Equal("id", 4)
        ));
        tableOrView.setProjections(List.of(
            Projection.plain("id"),
            Projection.from("role").with("id").as("role_id")
        ));
        System.out.println(tableOrView.toSqlCommand());

        try {
            tableOrView.validate();
            fail("此处的 SQL 应不合法");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
