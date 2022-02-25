package com.hxjg.vpn.api.bean.req.user;

import com.hxjg.vpn.api.bean.base.ApiModelProperty;
import com.hxjg.vpn.api.bean.base.BaseReq;

public class LoginByPhoneReq extends BaseReq {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("验证码")
    private String verificationCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
