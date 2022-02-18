package com.buzz.vpn.api.bean.req.app;

import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseReq;

import java.util.List;

public class AccelerateApplyReq extends BaseReq {

    @ApiModelProperty("应用id")
    private String applicationInfoId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("私网ip")
    private String privateIp;

    @ApiModelProperty("公网ip")
    private String publicIp;

    @ApiModelProperty("目标IP地址 要加速的指定IP地址。 注：目前最多支持4个ipv4地址")
    private List<String> targetIp;

    public String getApplicationInfoId() {
        return applicationInfoId;
    }

    public void setApplicationInfoId(String applicationInfoId) {
        this.applicationInfoId = applicationInfoId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public List<String> getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(List<String> targetIp) {
        this.targetIp = targetIp;
    }
}
