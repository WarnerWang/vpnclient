package com.hxjg.vpn.api.bean.base;

public class BasePageReq extends BaseReq {

    /**
     * from : 0
     * size : 0
     * start : 0
     */


    /**
     * 开始页码 1开始
     */
    private int from;
    /**
     * 数量
     */
    private int size;
    private int start;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
