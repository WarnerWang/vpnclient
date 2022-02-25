package com.hxjg.vpn.base;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
