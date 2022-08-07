package com.hyd.hybatis;

import com.hyd.hybatis.page.Pagination;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

    ///////////////////////////////////

    public Page(List<T> list) {
        this.list = list;
        this.totalRows = Pagination.Context.getInstance().getTotalRows();
        this.totalPages = Pagination.Context.getInstance().getTotalPages();
    }

    private final List<T> list;

    private final int totalRows;

    private final int totalPages;

    public List<T> getList() {
        return list;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalRows() {
        return totalRows;
    }
}
