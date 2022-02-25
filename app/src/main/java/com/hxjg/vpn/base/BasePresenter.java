package com.hxjg.vpn.base;

public interface BasePresenter<T extends BaseView> {

    void addView(T view);

    void removeView();

}
