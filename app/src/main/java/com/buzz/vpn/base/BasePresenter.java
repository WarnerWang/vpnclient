package com.buzz.vpn.base;

public interface BasePresenter<T extends BaseView> {

    void addView(T view);

    void removeView();

}
