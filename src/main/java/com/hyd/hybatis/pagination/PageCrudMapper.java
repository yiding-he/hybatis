package com.hyd.hybatis.pagination;

import com.github.pagehelper.Page;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.mapper.CrudMapper;

import javax.servlet.http.HttpServletRequest;

public interface PageCrudMapper<T> extends CrudMapper<T> {

    default PageHelperPage<T> selectPage(Conditions conditions, int pageNum, int pageSize) {
        return new PageHelperPage<>(pageNum, pageSize, () -> (Page<T>) selectList(conditions));
    }

    default PageHelperPage<T> selectPage(Conditions conditions, HttpServletRequest request) {
        return new PageHelperPage<>(request, () -> (Page<T>) selectList(conditions));
    }
}