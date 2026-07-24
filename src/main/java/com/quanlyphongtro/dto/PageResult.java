package com.quanlyphongtro.dto;

import java.util.List;

public class PageResult<T> {
    private List<T> items;
    private int total;
    private int page;
    private int totalPages;

    public PageResult() {}

    public PageResult(List<T> items, int total, int page, int totalPages) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
