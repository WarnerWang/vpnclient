package com.buzz.vpn.api.bean.req.app;

import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseReq;

public class AccelerateDeleteReq extends BaseReq {

    @ApiModelProperty("应用id")
    private String applicationInfoId;

    @ApiModelProperty("申请id")
    private String applyOrderId;

    @ApiModelProperty("手机号")
    private String mobile;

    public String getApplicationInfoId() {
        return applicationInfoId;
    }

    public void setApplicationInfoId(String applicationInfoId) {
        this.applicationInfoId = applicationInfoId;
    }

    public String getApplyOrderId() {
        return applyOrderId;
    }

    public void setApplyOrderId(String applyOrderId) {
        this.applyOrderId = applyOrderId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
