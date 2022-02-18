package com.buzz.vpn.api.bean.resp.user;

import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseResp;
import com.buzz.vpn.api.bean.commonBean.user.UserInfo;

public class LoginByPhoneResp extends BaseResp {

    @ApiModelProperty("密钥")
    private String dynamicSecret;

    @ApiModelProperty("用户信息")
    private UserInfo userInfo;

    public String getDynamicSecret() {
        return dynamicSecret;
    }

    public void setDynamicSecret(String dynamicSecret) {
        this.dynamicSecret = dynamicSecret;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
