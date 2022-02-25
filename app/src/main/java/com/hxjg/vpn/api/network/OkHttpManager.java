package com.hxjg.vpn.api.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.hxjg.vpn.api.bean.base.BaseResp;
import com.hxjg.vpn.api.exception.ServerError;
import com.hxjg.vpn.eventbus.IEvent;
import com.hxjg.vpn.manager.UserManage;
import com.hxjg.vpn.utils.AppUtil;
import com.hxjg.vpn.utils.DesUtils;
import com.hxjg.vpn.utils.JsonUtil;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.StringUtils;
import com.hxjg.vpn.utils.TokenCache;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

import static com.hxjg.vpn.api.network.Api.DEFAULT_DES_KEY;
import static com.hxjg.vpn.api.network.Api.HOST_WITH_DES;

/**
 */
public class OkHttpManager {

    private static final String TAG = OkHttpManager.class.getSimpleName();

    private volatile static OkHttpManager okHttpManager;

    private OkHttpClient.Builder getHttpsClientBuilder(){
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
        //创建管理器
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        TrustManager[] trustAllCerts = new TrustManager[] {x509TrustManager};
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            //为OkHttpClient设置sslSocketFactory
            okHttpClient.sslSocketFactory(sslContext.getSocketFactory(),x509TrustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return okHttpClient;
    }

    public OkHttpClient newClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpLogger());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = getHttpsClientBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        String param = "";
                        RequestBody requestBody = request.body();
                        String desParam = "";
                        if (requestBody != null) {
                            Buffer sink = new Buffer();
                            requestBody.writeTo(sink);
                            param = sink.readUtf8();
                            JSONObject jsonObject = new JSONObject();

                            String token = TokenCache.getIns().getAppToken();
//                            ToastUtil.ins().show("接口请求 ： "+TokenCache.getIns().getAppToken(),true);
                            Logger.i("接口请求 ： ",TokenCache.getIns().getAppToken());
                            Logger.i("method="+request.method());
                            if (TextUtils.isEmpty(token)) {
                                jsonObject.put("token", "");
                            } else {
                                jsonObject.put("token", token);
                            }

                            String userId = UserManage.ins().getUserId();
                            if (TextUtils.isEmpty(userId)) {
                                jsonObject.put("userId", "");
                            } else {
                                jsonObject.put("userId", userId);
                            }
                            String version = AppUtil.getVersionName();
                            if (TextUtils.isEmpty(version)) {
                                jsonObject.put("version", "");
                            } else {
                                String[] versions = version.split("-");
                                if (versions.length > 0) {
                                    String versionString = versions[0];
                                    jsonObject.put("version", versionString.replace("v",""));
                                }

                            }

                            if (HOST_WITH_DES) {
                                try {
                                    desParam = DesUtils.encryptDES(UserManage.ins().getSecretKey(), param, request.url().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            jsonObject.put("content", desParam);
//                            String jsonString = jsonObject.toJSONString();
//                            if (HOST_WITH_DES) {
//                                try {
//                                    jsonString = DesUtils.encryptDES(DEFAULT_DES_KEY, jsonString);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            RequestBody newRequestBody = RequestBody.create(requestBody.contentType(), desParam);
                            Logger.i(TAG, "**************************************** request.start ****************************************");
                            Request.Builder requestBuilder = request.newBuilder();
                            if (!StringUtils.isEmpty(userId)) {
                                requestBuilder = requestBuilder.addHeader("userId",userId);
                            }
                            request = requestBuilder
                                    .method(request.method(),newRequestBody)
                                    .build();
                        }

                        Response response;
                        try {
                            response = chain.proceed(request);
                        } catch (SocketTimeoutException exception) {
                            throw new ServerError(400, "网络连接超时");
                        } catch (Exception e) {
                            Logger.printException(e);
                            throw e;
                        }

                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String content = responseBody.string();
                            int errCode = 0;
                            String errMsg;
//                            Logger.i(TAG, "解密前:" + content);
//                            if (HOST_WITH_DES) {
//                                content = DesUtils.decryptDES(DEFAULT_DES_KEY, content);
//                            }

                            String requestUrl = request.url().toString();

                            Logger.i(TAG, "\nrequestUrl=" + requestUrl+",\nmethod="+request.method() + ",\nparam=" + param + ",\ndescParams=" + desParam + ",\ncontent:" + content);
                            Logger.i(TAG, "**************************************** request.end ****************************************");
                            String data;
                            try {
                                BaseResp baseResp = JsonUtil.parseObject(content, BaseResp.class);
                                errCode = baseResp.getCode();
                                errMsg = baseResp.getMsg();
                                JSONObject respData = baseResp.getData();
                                if (respData != null) {
                                    respData.put("code",errCode);
                                    respData.put("msg",errMsg);
                                    data = respData.toJSONString();
                                }else {
                                    data = content;
                                }
                            } catch (Exception e) {
                                throw new ServerError(errCode, "\ncontent:" + content + "\n" + " url:" + request.url().toString() + "\n" + e.getMessage());
                            }
                            if (errCode != 200) {
                                Logger.e("server return:" + content);
                                Logger.e("server return error:" + errCode + ", msg:" + errMsg + ", url:" + request.url().toString());
                                if ("6888".equals(errCode)) {
                                    EventBus.getDefault().post(new IEvent.Logout(true));
                                }
                                throw new ServerError(errCode, errMsg);
                            }

                            ResponseBody newResponseBody = ResponseBody
                                    .create(responseBody.contentType(), data);
                            response = response.newBuilder()
                                    .body(newResponseBody)
                                    .build();
                        }
                        return response;
                    }
                })
                .addInterceptor(interceptor)
                .build();
        return okHttpClient;
    }

    private OkHttpManager() {
    }

    public OkHttpClient newWeChatClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpLogger());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        return okHttpClient;
    }

    public static OkHttpManager getSingleton() {
        if (okHttpManager == null) {
            synchronized (OkHttpManager.class) {
                if (okHttpManager == null) {
                    okHttpManager = new OkHttpManager();
                }
            }
        }
        return okHttpManager;
    }

    private static final class OkHttpLogger implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(String message) {
            if (HOST_WITH_DES) {
                message = DesUtils.decryptDES(DEFAULT_DES_KEY, message);
            }
//            Logger.i(TAG, "" + message);
        }

    }
}
