package com.hxjg.vpn.api.repository;

import com.hxjg.vpn.api.bean.base.BaseResp;
import com.hxjg.vpn.api.bean.req.app.AccelerateApplyReq;
import com.hxjg.vpn.api.bean.req.app.AccelerateDeleteReq;
import com.hxjg.vpn.api.bean.resp.app.AccelerateAppListResp;
import com.hxjg.vpn.api.bean.resp.app.AccelerateApplyResp;
import com.hxjg.vpn.api.bean.resp.app.QueryApplicationInfoListResp;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface AppAPI {

    @GET("product-app/accelerate/queryApplicationInfoList")
    Observable<QueryApplicationInfoListResp> queryApplicationInfoList();

    @GET("product-app/accelerate/accelerateAppList")
    Observable<AccelerateAppListResp> accelerateAppList();

    @POST("product-app/accelerate/accelerateApply")
    Observable<AccelerateApplyResp> accelerateApply(@Body AccelerateApplyReq req);

    @POST("product-app/accelerate/accelerateDelete")
    Observable<BaseResp> accelerateDelete(@Body AccelerateDeleteReq req);
}
