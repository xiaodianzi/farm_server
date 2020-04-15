package com.plansolve.farm.util;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Andrew
 * @Date: 2018/8/17
 * @Description:
 */
public class PagerUtil<T> implements Serializable {
    //页面参数
    private Integer pageNumber;//当前页
    private Integer pageSize;//每页显示多少条

    //查询数据库
    private Integer total;//总记录数
    private List<T> rows;//本页的数据列表

    //计算
    private Integer totalPage;//总页数
    private Integer beginPageIndex;//页码列表的开始索引
    private Integer endPageIndex;//页码列表的结束索引

    public PagerUtil(){};

    /**
     * 只接受前4个必要的属性，会自动计算出其他3个属性的值
     * @param pageNumber
     * @param pageSize
     * @param total
     * @param rows
     */
    public PagerUtil(Integer pageNumber, Integer pageSize, Integer total,
                    List rows) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.total = total;
        this.rows = rows;

        //计算总页码
        totalPage = (total + pageSize - 1) / pageSize;

        //计算beginPageIndex 和 endPageIndexH
        //>>总页数不多于10页，则全部显示
        if (totalPage <= 10) {
            beginPageIndex = 1;
            endPageIndex = totalPage;
        }
        //总页数多于10页，则显示当前页附近的共10个页码
        else {
            //当前页附近的共10个页码（前4个+当前页+后5个）
            beginPageIndex = pageNumber - 4;
            endPageIndex = pageNumber + 5;

            //当前面的页码不足4个时，则显示前10个页码
            if (beginPageIndex < 1) {
                beginPageIndex = 1;
                endPageIndex = 10;
            }
            //当后面的页码不足5个时，则显示后10个页码
            if (endPageIndex > totalPage) {
                endPageIndex = totalPage;
                beginPageIndex = totalPage - 10 + 1;
            }
        }
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getBeginPageIndex() {
        return beginPageIndex;
    }

    public void setBeginPageIndex(Integer beginPageIndex) {
        this.beginPageIndex = beginPageIndex;
    }

    public Integer getEndPageIndex() {
        return endPageIndex;
    }

    public void setEndPageIndex(Integer endPageIndex) {
        this.endPageIndex = endPageIndex;
    }

    //获取下一页
    public long getNextPage(long curPage, long totalPage){
        if(curPage+1 >=totalPage){
            return totalPage;
        }
        return curPage+1;
    }

    //获取上一页
    public long getPrevPage(long curPage){
        if(curPage-1 <= 0){
            return 0;
        }
        return curPage-1;
    }

}
