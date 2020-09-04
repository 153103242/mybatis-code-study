package com.yc.study.plugin;

/**
 * @author admin
 * @date 2020/9/2 12:42:29
 * @description
 */
public class Page {

    private Integer total;  // 总页数
    private Integer size;   // 每页大小
    private Integer index;  // 页码 从1开始

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getIndex() {
        return index;
    }

    public Page() {
    }
    //计算起始坐标
    public int getOffset(){
        return size*(index-1);
    }
}
