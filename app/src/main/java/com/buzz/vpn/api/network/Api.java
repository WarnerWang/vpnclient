package com.buzz.vpn.api.network;

import com.buzz.vpn.BuildConfig;
import com.buzz.vpn.api.repository.AppAPI;
import com.buzz.vpn.api.repository.UserAPI;
import com.buzz.vpn.utils.TokenCache;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * http://www.hexinjingu.com/api/sports/
 * <p>
 * http://www.hexinjingu.com/zentaopms/www/my-todo.html
 * bugs
 */
public class Api {
    public static final String TAG = Api.class.getSimpleName();

    public enum ApiType {
        dev,
        test,
        buss,
        release
    }

    public static final ApiType API_TYPE = ApiType.valueOf(BuildConfig.API_TYPE);

    public static final Boolean HOST_WITH_DES = true;//是否使用加密

    public static final String DEFAULT_DES_KEY = "hxsports";//默认加密秘钥

    private static final String HOST_DEBUG = "https://42.236.3.123:9080/product-gateway-app/";
    private static final String HOST_TEST = "https://42.236.3.123:9080/product-gateway-app/";
    private static final String BUSS_TEST = "https://42.236.3.123:9080/product-gateway-app/";
    private static final String HOST_RELEASE = "https://42.236.3.123:9080/product-gateway-app/";

    private static final String HOST_DEBUG_DES = "https://42.236.3.123:9080/product-gateway-app/";
//    private static final String HOST_TEST_DES = "http://www.hexinjingu.com/sports_web_mobile_transfer/main/";
    private static final String HOST_TEST_DES = "https://42.236.3.123:9080/product-gateway-app/";
    private static final String HOST_BUSS_DES = "https://42.236.3.123:9080/product-gateway-app/";

//    private static final String HOST_TEST_DES = "http://www.hexinjingu.com/sports_web_mobile_transfer/newmain/";
    private static final String HOST_RELEASE_DES = "https://42.236.3.123:9080/product-gateway-app/";
//    private static final String HOST_RELEASE_DES = "https://www.88lot.com/sports_web_mobile_transfer/newmain/";

    private static final String HOST_WECHAT = "https://api.weixin.qq.com/";


    //************************************************************
    private volatile static Api api;

    private UserAPI userAPI;
    private AppAPI appAPI;

    public static Api ins() {
        if (api == null) {
            synchronized (Api.class) {
                if (api == null) {
                    api = new Api();
                }
            }
        }
        return api;
    }

    private Api() {
        updateApi();
    }

    public boolean isDebug() {
        if (BuildConfig.DEBUG || API_TYPE.equals(ApiType.dev) || API_TYPE.equals(ApiType.test) || API_TYPE.equals(ApiType.buss)) {
            return true;
        } else {
            return false;
        }
    }

    public String getBaseUrl() {
        String url = HOST_DEBUG;
        switch (API_TYPE) {
            case dev:
                url = HOST_WITH_DES ? HOST_DEBUG_DES : HOST_DEBUG;
                break;
            case test:
                url = HOST_WITH_DES ? HOST_TEST_DES : HOST_TEST;

                break;
            case buss:
                url = HOST_WITH_DES ? HOST_BUSS_DES : BUSS_TEST;
                break;
            case release:
                url = HOST_WITH_DES ? HOST_RELEASE_DES : HOST_RELEASE;
                break;
            default:
                url = HOST_WITH_DES ? HOST_DEBUG_DES : HOST_DEBUG;
//                url = HOST_TEST_DES;
                break;
        }
        if (isDebug()) {
            int urlType = TokenCache.getIns().getLoadUrlType();
            if (urlType == 1) {
                url = HOST_WITH_DES ? HOST_DEBUG_DES : HOST_DEBUG;
            }else if (urlType == 2) {
                url = HOST_WITH_DES ? HOST_TEST_DES : HOST_TEST;
            }else if (urlType == 4) {
                url = HOST_WITH_DES ? HOST_BUSS_DES : BUSS_TEST;
            }else if (urlType == 3) {
                url = HOST_WITH_DES ? HOST_RELEASE_DES : HOST_RELEASE;
            }
        }
        url = HOST_WITH_DES ? HOST_DEBUG_DES : HOST_DEBUG;
        return url;
    }

    public static int getDefaultLoadUrlType(){
        if (API_TYPE.equals(ApiType.dev)) {
            return 1;
        }else if (API_TYPE.equals(ApiType.test)) {
            return 2;
        }else if (API_TYPE.equals(ApiType.buss)) {
            return 4;
        }else {
            return 3;
        }
    }

    public void updateApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(OkHttpManager.getSingleton().newClient())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        userAPI = retrofit.create(UserAPI.class);
        appAPI = retrofit.create(AppAPI.class);
    }

    public UserAPI getUserAPI() {
        return userAPI;
    }

    public void setUserAPI(UserAPI userAPI) {
        this.userAPI = userAPI;
    }

    public AppAPI getAppAPI() {
        return appAPI;
    }

    public void setAppAPI(AppAPI appAPI) {
        this.appAPI = appAPI;
    }
}

