package com.buzz.vpn.api.bean.commonBean.user;

import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseEntity;

public class UserInfo extends BaseEntity {

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("状态 0-正常 1-冻结 2-删除")
    private Integer status;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
