package com.buzz.vpn.base;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
