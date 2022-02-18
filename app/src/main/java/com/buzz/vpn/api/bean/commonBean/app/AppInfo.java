package com.buzz.vpn.api.bean.commonBean.app;

import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseEntity;

public class AppInfo extends BaseEntity {

    @ApiModelProperty("应用id")
    private String applicationInfoId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("应用图标")
    private String applicationIcon;

    @ApiModelProperty("应用状态")
    private String applicationStatus;

    private String productId;

    private String targetIp;

    private String createTime;

    private String updateTime;

    private String bundleId;

    @JSONField(serialize = false)
    private Drawable appDrawable;

    public String getApplicationInfoId() {
        return applicationInfoId;
    }

    public void setApplicationInfoId(String applicationInfoId) {
        this.applicationInfoId = applicationInfoId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationIcon() {
        return applicationIcon;
    }

    public void setApplicationIcon(String applicationIcon) {
        this.applicationIcon = applicationIcon;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public Drawable getAppDrawable() {
        return appDrawable;
    }

    public void setAppDrawable(Drawable appDrawable) {
        this.appDrawable = appDrawable;
    }
}
