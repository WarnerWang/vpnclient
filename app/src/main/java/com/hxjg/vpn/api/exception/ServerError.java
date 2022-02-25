package com.hxjg.vpn.api.exception;

import java.io.IOException;

public class ServerError extends IOException {
    private int errorCode;
    private String msg;

    public ServerError(int errorCode, String msg) {
        super("server return error:" + errorCode + ",msg:" + msg);
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
