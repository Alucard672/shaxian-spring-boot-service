package com.shaxian.biz.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "分页查询结果")
public class PageResult<T> {
    @Schema(description = "数据列表")
    private List<T> items;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize;

    @Schema(description = "总页数", example = "5")
    private Integer totalPages;

    public PageResult() {
    }

    public PageResult(List<T> items, Long total, Integer pageNo, Integer pageSize) {
        this.items = items;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> of(List<T> items, Long total, Integer pageNo, Integer pageSize) {
        return new PageResult<>(items, total, pageNo, pageSize);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}

