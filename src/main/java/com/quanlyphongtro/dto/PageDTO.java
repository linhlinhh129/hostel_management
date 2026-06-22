package com.quanlyphongtro.dto;

import java.util.List;

public class PageDTO<T> {
    private List<T> items;
    private int page;       // 1-based for display
    private int pageSize;
    private long total;

    public PageDTO() {}

    public PageDTO(List<T> items, int page, int pageSize, long total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getTotalPages() {
        if (pageSize <= 0) return 1;
        return (int) Math.ceil((double) total / pageSize);
    }

    public boolean hasPreviousPage() { return page > 1; }
    public boolean hasNextPage() { return page < getTotalPages(); }
}
