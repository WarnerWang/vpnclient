package com.hxjg.vpn.api.bean.base;

import com.hxjg.vpn.utils.JsonUtil;

import java.io.Serializable;

public class BaseEntity implements Serializable{

    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}
