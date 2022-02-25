package com.hxjg.vpn.api.bean.req.user;

import com.hxjg.vpn.api.bean.base.ApiModelProperty;
import com.hxjg.vpn.api.bean.base.BaseReq;

public class GetSmsCodeReq extends BaseReq {

    @ApiModelProperty("手机号")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
