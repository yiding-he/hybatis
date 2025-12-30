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

    public static <T> PageHelperPage<T> fromList(HttpServletRequest request, Supplier<List<T>> listSupplier) {
        return new PageHelperPage<>(request,() -> {
            var list = listSupplier.get();
            if (list instanceof Page) {
                return (Page<T>) list;
            } else {
                var page = new Page<T>(1, list.size());
                page.addAll(list);
                return page;
            }
        });
    }

    public PageHelperPage(HttpServletRequest request, Supplier<Page<T>> pageSupplier) {
        var pageNum = request.getParameter("pageNum") == null ? 1 : Integer.parseInt(request.getParameter("pageNum"));
        var pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
        var page = runQuery(pageNum, pageSize, pageSupplier);
        setupPage(pageNum, pageSize, page);
    }

    public PageHelperPage(int pageNum, int pageSize, Supplier<Page<T>> pageSupplier) {
        var page = runQuery(pageNum, pageSize, pageSupplier);
        setupPage(pageNum, pageSize, page);
    }

    private List<T> list;

    private int total;

    private int pages;

    private int pageNum;

    private int pageSize;

    private Page<T> runQuery(int pageNum, int pageSize, Supplier<Page<T>> pageSupplier) {
        try (var ignored = PageHelper.startPage(pageNum, pageSize)) {
            return pageSupplier.get();
        }
    }

    private void setupPage(int pageNum, int pageSize, Page<T> page) {
        this.list = new ArrayList<>(page);
        this.total = (int) page.getTotal();
        this.pages = page.getPages();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
