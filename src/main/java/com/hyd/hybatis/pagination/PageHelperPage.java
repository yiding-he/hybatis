package com.hyd.hybatis.pagination;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Integrate with <a href="https://github.com/pagehelper/Mybatis-PageHelper">Mybatis-PageHelper</a>
 *
 * @param <T> Entity type
 */
@Data
public class PageHelperPage<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 43212L;

    @SuppressWarnings("resource")
    public PageHelperPage(HttpServletRequest request, Supplier<Page<T>> pageSupplier) {
        var pageNum = request.getParameter("pageNum") == null ? 1 : Integer.parseInt(request.getParameter("pageNum"));
        var pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
        PageHelper.startPage(pageNum, pageSize);
        var page = pageSupplier.get();
        this.list = new ArrayList<>(page);
        this.total = (int) page.getTotal();
        this.pages = page.getPages();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    @SuppressWarnings("resource")
    public PageHelperPage(int pageNum, int pageSize, Supplier<Page<T>> pageSupplier) {
        PageHelper.startPage(pageNum, pageSize);
        var page = pageSupplier.get();
        this.list = new ArrayList<>(page);
        this.total = (int) page.getTotal();
        this.pages = page.getPages();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    private final List<?> list;

    private final int total;

    private final int pages;

    private final int pageNum;

    private final int pageSize;
}
