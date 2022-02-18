package com.buzz.vpn.api.repository;

import com.buzz.vpn.api.bean.base.BaseResp;
import com.buzz.vpn.api.bean.req.app.AccelerateAppListReq;
import com.buzz.vpn.api.bean.req.app.AccelerateApplyReq;
import com.buzz.vpn.api.bean.req.app.AccelerateDeleteReq;
import com.buzz.vpn.api.bean.req.app.QueryApplicationInfoListReq;
import com.buzz.vpn.api.bean.resp.app.AccelerateAppListResp;
import com.buzz.vpn.api.bean.resp.app.AccelerateApplyResp;
import com.buzz.vpn.api.bean.resp.app.QueryApplicationInfoListResp;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
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
