package com.buzz.vpn.api.bean.base;

import com.buzz.vpn.utils.JsonUtil;

import java.io.Serializable;

public class BaseEntity implements Serializable{

    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}
