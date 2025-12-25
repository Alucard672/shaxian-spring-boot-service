package com.shaxian.biz.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 分页查询结果
 * 用于封装分页查询的返回数据，包含数据列表和分页信息
 *
 * @param <T> 数据项的类型
 */
@Schema(description = "分页查询结果")
public class PageResult<T> {
    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> items;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Integer pageNo;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "20")
    private Integer pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "5")
    private Integer totalPages;

    public PageResult() {
    }

    /**
     * 构造函数
     *
     * @param items   数据列表
     * @param total   总记录数
     * @param pageNo  当前页码
     * @param pageSize 每页条数
     */
    public PageResult(List<T> items, Long total, Integer pageNo, Integer pageSize) {
        this.items = items;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 创建分页结果对象
     *
     * @param items    数据列表
     * @param total     总记录数
     * @param pageNo   当前页码
     * @param pageSize 每页条数
     * @param <T>      数据项类型
     * @return 分页结果对象
     */
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

