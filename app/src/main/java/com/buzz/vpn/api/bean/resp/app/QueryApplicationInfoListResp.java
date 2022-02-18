package com.buzz.vpn.api.bean.resp.app;

import com.buzz.vpn.api.bean.base.ApiModelProperty;
import com.buzz.vpn.api.bean.base.BaseResp;
import com.buzz.vpn.api.bean.commonBean.app.AppInfo;

import java.util.List;

public class QueryApplicationInfoListResp extends BaseResp {

    @ApiModelProperty("应用信息")
    private List<AppInfo> list;

    public List<AppInfo> getList() {
        return list;
    }

    public void setList(List<AppInfo> list) {
        this.list = list;
    }
}
