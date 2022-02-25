package com.hxjg.vpn.manager;

import android.text.TextUtils;

import com.hxjg.vpn.api.bean.base.BaseResp;
import com.hxjg.vpn.api.bean.commonBean.user.UserInfo;
import com.hxjg.vpn.api.bean.req.user.LogoutReq;
import com.hxjg.vpn.api.network.Api;
import com.hxjg.vpn.eventbus.IEvent;
import com.hxjg.vpn.utils.JsonUtil;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.StringUtils;
import com.hxjg.vpn.utils.ToastUtil;
import com.hxjg.vpn.utils.TokenCache;

import org.greenrobot.eventbus.EventBus;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserManage {
    public enum LoginType {
        Phone, WeChat
    }

    private static volatile UserManage ins;
    private boolean ignoreUpdate;

    private String secretKey;
    private UserInfo userInfo;

    private UserManage() {
    }

    public static UserManage ins() {
        if (ins == null) {
            synchronized (UserManage.class) {
                if (ins == null) {
                    ins = new UserManage();
                }
            }
        }
        return ins;
    }

    public static boolean isLoggedIn() {
        String secretKey = TokenCache.getIns().getSecretKey();
        return !TextUtils.isEmpty(secretKey);
    }

    public String getUserId() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            return userInfo.getUserId();
        }
        return null;
    }

    public boolean isIgnoreUpdate() {
        return ignoreUpdate;
    }

    public void setIgnoreUpdate(boolean ignoreUpdate) {
        this.ignoreUpdate = ignoreUpdate;
    }


    public String getSecretKey() {
        if (StringUtils.isEmpty(secretKey)) {
            secretKey = TokenCache.getIns().getSecretKey();
        }
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        TokenCache.getIns().saveSecretKey(secretKey);
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            String userInfoStr = TokenCache.getIns().getUserInfo();
            if (!TextUtils.isEmpty(userInfoStr)) {
                try {
                    userInfo = JsonUtil.parseObject(userInfoStr, UserInfo.class);
                } catch (Exception e) {
                    Logger.printException(e);
                }
            }
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo != null) {
            TokenCache.getIns().setUserInfo(JsonUtil.toJSONString(userInfo));
        } else {
            TokenCache.getIns().setUserInfo(null);
        }
    }

    public void logout(){
        LogoutReq req = new LogoutReq();
        req.setUserId(getUserId());
        Subscription subscription = Api.ins()
                .getUserAPI()
                .logout(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        ToastUtil.ins().show(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp getSmsCodeResp) {
                        EventBus.getDefault().post(new IEvent.Logout(true));
                    }
                });

    }
}
