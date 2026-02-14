package com.hyd.hybatis.query;

import com.hyd.hybatis.sql.SqlCommand;

public interface Filter {

    SqlCommand toSqlCommand();
}
