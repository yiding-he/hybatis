package com.hyd.hybatis.mappers;

import com.hyd.hybatis.pagination.PageCrudMapper;
import com.hyd.hybatis.row.Row;

/**
 * 测试如果有一个项目间接封装 PageCrudMapper，它是否正正常运作
 */
public interface BasePageCrudMapper extends PageCrudMapper<Row> {

}
