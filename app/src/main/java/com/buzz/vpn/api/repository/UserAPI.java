package com.buzz.vpn.api.repository;

import com.buzz.vpn.api.bean.base.BaseResp;
import com.buzz.vpn.api.bean.req.user.GetSmsCodeReq;
import com.buzz.vpn.api.bean.req.user.LoginByPhoneReq;
import com.buzz.vpn.api.bean.req.user.LogoutReq;
import com.buzz.vpn.api.bean.resp.user.GetSmsCodeResp;
import com.buzz.vpn.api.bean.resp.user.LoginByPhoneResp;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface UserAPI {

    /**
     * 获取手机验证码
     */
    @POST("product-app/user/getSmsCode")
    Observable<GetSmsCodeResp> getSmsCode(@Body GetSmsCodeReq getSmsCodeReq);

    /**
     * 获取手机验证码
     */
    @POST("product-app/user/loginByPhone")
    Observable<LoginByPhoneResp> loginByPhone(@Body LoginByPhoneReq loginByPhoneReq);

    /**
     * 获取手机验证码
     */
    @POST("product-app/user/logout")
    Observable<BaseResp> logout(@Body LogoutReq logoutReq);
}
