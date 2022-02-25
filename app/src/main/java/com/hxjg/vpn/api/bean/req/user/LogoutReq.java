package com.hxjg.vpn.api.bean.req.user;

import com.hxjg.vpn.api.bean.base.BaseReq;

public class LogoutReq extends BaseReq {

    private String userId;

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
