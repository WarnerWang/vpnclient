package com.buzz.vpn.api.bean.base;

import com.alibaba.fastjson.JSONObject;
import com.buzz.vpn.utils.JsonUtil;

import java.io.Serializable;

public class BaseResp implements Serializable {


    /**
     * errCode : string
     * errMsg : string
     */

    private int code = 200;
    private String msg = "成功";

    private JSONObject data;

    public BaseResp() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString() {

        return JsonUtil.toJSONString(this);
    }
}
