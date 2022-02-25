package com.hxjg.vpn.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hxjg.vpn.api.network.Api;

import de.blinkt.openvpn.core.App;


/**
 * Created by xiaoming on 16/7/27.
 */
public class TokenCache {
    private static final String TAG = TokenCache.class.getSimpleName();
    private static final String KEY_WECHAT_ACCESS_TOKEN = "WECHAT_ACCESS_TOKEN";
    private static final String KEY_WECHAT_REFRESH_TOKEN = "WECHAT_REFRESH_TOKEN";
    private static final String KEY_WECHAT_OPENID = "WECHAT_OPENID";
    private static final String KEY_APP_TOKEN = "APP_TOKEN";
    private static final String KEY_USER_LOGIN_INFO = "USER_LOGIN_INFO";
    private static final String KEY_USER_INFO = "KEY_USER_INFO";
    private static final String KEY_XINGE_TOKEN = "XINGE_TOKEN";
    private static final String KEY_LOGIN_TYPE = "LOGIN_TYPE";
    private static final String KEY_APP_CONFIG = "APP_CONFIG";
    private static final String KEY_APP_AD = "KEY_APP_AD_";
    private static final String KEY_NET_IP = "NET_IP";//ip地址
    private static final String KEY_LOAD_URL_TYPE = "LOAD_URL_TYPE";
    private static final String KEY_DEVICE_ID = "DEVICE_ID";//设备id
    private static final String KEY_DOWNLOAD_APK_RESP = "DOWNLOAD_APK_RESP";//新版本apk下载成功后存储的新版本信息
    private static final String KEY_DOWNLOAD_APK_FILENAME = "DOWNLOAD_APK_FILENAME";//新版本apk下载成功后保存的文件名称
    private static final String KEY_SPLASH_AD_BITMAP_FILE_PATH = "KEY_SPLASH_AD_BITMAP_FILE_PATH";//启动页广告图片本地路径
    private static final String KEY_SHOW_PRIVACY_AGREEMENT_INFO = "KEY_SHOW_PRIVACY_AGREEMENT_INFO";//是否显示过隐私权限弹窗
    private static final String KEY_SECRET_KEY = "KEY_SECRET_KEY";//接口DES密钥
    private static final String KEY_CUR_USE_VPN_PACKAGE_NAME = "KEY_CUR_USE_VPN_PACKAGE_NAME";//当前使用vpn的应用包名
    private static final int TIME_MATCH_LEAGUE_COLOR_SAVE = 60 * 60 *24;//1天
    private static final int TIME_MATCH_LEAGUE_FILTER_SAVE = 60 * 60 *12;//12小时
    private static final int TIME_WECHAT_ACCESS_TOKEN_SAVE = 60 * 60; //1小时
    private static final int TIME_WECHAT_REFRESH_TOKEN_SAVE = 60 * 60 * 24 * 30; //30天
    private static final int TIME_WECHAT_OPENID_SAVE = 60 * 60 * 24 * 30; //30天
    private static final int TIME_APP_TOKEN_SAVE = 60 * 60 * 24 * 30; //30天
    private static final int TIME_XINGE_SAVE = 60 * 60 * 24 * 30; //30天
    private static final int TIME_WILL_USER_COUPON_SAVE = 60 * 10; //10分钟
    private static final int TIME_OPEN_PUSH_SAVE = 60 * 60 * 16;//16小时
    private static volatile TokenCache tokenCache;
    private ACache aCache;

    private TokenCache(Context context) {
        if (context == null) {
            return;
        }
        aCache = ACache.get(context);
    }

    public static TokenCache getIns() {
        if (tokenCache == null) {
            synchronized (TokenCache.class) {
                if (tokenCache == null) {
                    tokenCache = new TokenCache(App.ins());
                }
            }
        }
        return tokenCache;
    }

    public void setWechatAccessToken(String token) {
        if (TextUtils.isEmpty(token)) {
            aCache.remove(KEY_WECHAT_ACCESS_TOKEN);
        } else {
            aCache.put(KEY_WECHAT_ACCESS_TOKEN, token, TIME_WECHAT_ACCESS_TOKEN_SAVE);
        }
    }

    public String getWechatAccessToken() {
        return aCache.getAsString(KEY_WECHAT_ACCESS_TOKEN);
    }

    public void setWechatRefreshToken(String token) {
        if (TextUtils.isEmpty(token)) {
            aCache.remove(KEY_WECHAT_REFRESH_TOKEN);
        } else {
            aCache.put(KEY_WECHAT_REFRESH_TOKEN, token, TIME_WECHAT_REFRESH_TOKEN_SAVE);
        }
    }

    public String getWechatRefreshToken() {
        return aCache.getAsString(KEY_WECHAT_REFRESH_TOKEN);
    }

    public void setWechatOpenId(String openId) {
        if (TextUtils.isEmpty(openId)) {
            aCache.remove(KEY_WECHAT_OPENID);
        } else {
            aCache.put(KEY_WECHAT_OPENID, openId, TIME_WECHAT_OPENID_SAVE);
        }
    }

    public String getWechatOpenId() {
        return aCache.getAsString(KEY_WECHAT_OPENID);
    }

    public void setAppToken(String appToken) {
        if (TextUtils.isEmpty(appToken)) {
            aCache.remove(KEY_APP_TOKEN);
        } else {
            aCache.put(KEY_APP_TOKEN, appToken, TIME_APP_TOKEN_SAVE);
        }
    }

    public String getAppToken() {
        return aCache.getAsString(KEY_APP_TOKEN);
    }

    public void setLoginUserInfo(String loginUserInfo) {
        if (TextUtils.isEmpty(loginUserInfo)) {
            aCache.remove(KEY_USER_LOGIN_INFO);
        } else {
            aCache.put(KEY_USER_LOGIN_INFO, loginUserInfo, TIME_APP_TOKEN_SAVE);
        }
    }

    public String getLoginUserInfo() {
        return aCache.getAsString(KEY_USER_LOGIN_INFO);
    }

    public void setUserInfo(String userInfo) {
        if (TextUtils.isEmpty(userInfo)) {
            aCache.remove(KEY_USER_INFO);
        } else {
            aCache.put(KEY_USER_INFO, userInfo, TIME_APP_TOKEN_SAVE);
        }
    }

    public String getUserInfo() {
        return aCache.getAsString(KEY_USER_INFO);
    }

    public void setXingeToken(String token) {
        if (TextUtils.isEmpty(token)) {
            aCache.remove(KEY_XINGE_TOKEN);
        } else {
            aCache.put(KEY_XINGE_TOKEN, token, TIME_XINGE_SAVE);
        }
    }

    public String getXingeToken() {
        return aCache.getAsString(KEY_XINGE_TOKEN);
    }

    public void setLoginType(String type) {
        if (TextUtils.isEmpty(type)) {
            aCache.remove(KEY_LOGIN_TYPE);
        } else {
            aCache.put(KEY_LOGIN_TYPE, type);
        }
    }

    public String getLoginType() {
        return aCache.getAsString(KEY_LOGIN_TYPE);
    }


    public void setAppConfig(String appConfig) {
        if (TextUtils.isEmpty(appConfig)) {
            aCache.remove(KEY_APP_CONFIG);
        } else {
            aCache.put(KEY_APP_CONFIG, appConfig);
        }
    }

    public String getAppConfig() {
        return aCache.getAsString(KEY_APP_CONFIG);
    }


    /**
     * 保存ip地址
     * @param value ip地址
     */
    public void setNetIpAddress(String value){
        if (TextUtils.isEmpty(value)) {
            aCache.remove(KEY_NET_IP);
        }else {
            aCache.put(KEY_NET_IP, value);
        }
    }

    /**
     * 获取ip地址
     * @return ip地址
     */
    public String getNetIpAddress(){
        return aCache.getAsString(KEY_NET_IP);
    }


    /**
     * 保存接口请求类型
     * @param urlType 0 默认 1 - 开发 2 - 测试 3 - 线上 4-商务
     */
    public void saveLoadUrlType(int urlType){
        aCache.put(KEY_LOAD_URL_TYPE,urlType+"");
    }

    /**
     * 获取接口请求类型
     * @return
     */
    public int getLoadUrlType(){
        String value = aCache.getAsString(KEY_LOAD_URL_TYPE);
        if (StringUtils.isEmpty(value)) {
            return Api.getDefaultLoadUrlType();
        }
        Integer urlType = Integer.valueOf(value);
        if (urlType == null) {
            return Api.getDefaultLoadUrlType();
        }
        return urlType;
    }

    /**
     * 保存设备id
     * @param deviceId
     */
    public void saveDeviceId(String deviceId){
        if (StringUtils.isEmpty(deviceId)) {
            aCache.remove(KEY_DEVICE_ID);
        }else {
            aCache.put(KEY_DEVICE_ID, deviceId);
        }
    }

    /**
     * 获取设备id
     * @return
     */
    public String getDeviceId(){
        return aCache.getAsString(KEY_DEVICE_ID);
    }

    /**
     * 保存设备mac地址
     * @param macAddress
     */
    public void saveMacAddress(String macAddress){
        if (StringUtils.isEmpty(macAddress)) {
            aCache.remove("KEY_MAC_ADDRESS");
        }else {
            aCache.put("KEY_MAC_ADDRESS", macAddress);
        }
    }

    /**
     * 获取设备mac地址
     * @return
     */
    public String getMacAddress(){
        return aCache.getAsString("KEY_MAC_ADDRESS");
    }

    /**
     * 保存设备id
     * @param imei
     */
    public void saveImei(String imei){
        if (StringUtils.isEmpty(imei)) {
            aCache.remove("KEY_IMEI");
        }else {
            aCache.put("KEY_IMEI", imei);
        }
    }

    /**
     * 获取设备id
     * @return
     */
    public String getImei(){
        return aCache.getAsString("KEY_IMEI");
    }


    /**
     * 保存设备id
     * @param ua
     */
    public void saveUA(String ua){
        if (StringUtils.isEmpty(ua)) {
            aCache.remove("KEY_UA");
        }else {
            aCache.put("KEY_UA", ua);
        }
    }

    /**
     * 获取设备id
     * @return
     */
    public String getUA(){
        return aCache.getAsString("KEY_UA");
    }

//    /**
//     * 保存新版本apk下载成功后存储的新版本信息
//     * @param resp
//     */
//    public void saveDownloadApkResp(GetNewestVersionResp resp){
//        if (resp == null) {
//            aCache.remove(KEY_DOWNLOAD_APK_RESP);
//        }else {
//            aCache.put(KEY_DOWNLOAD_APK_RESP,JsonUtil.toJSONString(resp));
//        }
//    }
//
//    /**
//     * 获取新版本apk下载成功后存储的新版本信息
//     * @return
//     */
//    public GetNewestVersionResp getDownloadApkResp(){
//
//        String value = aCache.getAsString(KEY_DOWNLOAD_APK_RESP);
//        if (!StringUtils.isEmpty(value)) {
//            try {
//                GetNewestVersionResp resp = JsonUtil.parseObject(value,GetNewestVersionResp.class);
//                return resp;
//            }catch (Exception e) {
//                Logger.printException(e);
//            }
//        }
//        return null;
//    }

    /**
     * 保存新版本apk下载成功后存储的apk文件名
     * @param fileName
     */
    public void saveDownloadApkFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            aCache.remove(KEY_DOWNLOAD_APK_FILENAME);
        }else {
            aCache.put(KEY_DOWNLOAD_APK_FILENAME, fileName);
        }
    }

    /**
     * 获取新版本apk下载成功后存储的apk文件名
     * @return
     */
    public String getDownloadApkFileName(){
        return aCache.getAsString(KEY_DOWNLOAD_APK_FILENAME);
    }

    /**
     * 启动页广告图片本地路径
     * @param filePath
     */
    public void saveSplashAdFilePath(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            aCache.remove(KEY_SPLASH_AD_BITMAP_FILE_PATH);
        }else {
            aCache.put(KEY_SPLASH_AD_BITMAP_FILE_PATH, filePath, TIME_WECHAT_REFRESH_TOKEN_SAVE);
        }
    }

    /**
     * 启动页广告图片本地路径
     * @return
     */
    public String getSplashAdFilePath(){
        return aCache.getAsString(KEY_SPLASH_AD_BITMAP_FILE_PATH);
    }

    /**
     * 是否显示过隐私权限弹窗
     * @param allow
     */
    public void saveShowPrivacyAgreementInfo(Boolean allow){

        aCache.put(KEY_SHOW_PRIVACY_AGREEMENT_INFO, allow ? "1" : "0");
    }

    /**
     * 是否显示过隐私权限弹窗
     * @return
     */
    public Boolean getShowPrivacyAgreementInfo(){
        String value = aCache.getAsString(KEY_SHOW_PRIVACY_AGREEMENT_INFO);
        if ("1".equals(value)) {
            return true;
        }
        return false;
    }

    public void saveSecretKey(String secretKey){
        if (StringUtils.isEmpty(secretKey)) {
            aCache.remove(KEY_SECRET_KEY);
        }else {
            aCache.put(KEY_SECRET_KEY, secretKey);
        }
    }

    public String getSecretKey(){
        return aCache.getAsString(KEY_SECRET_KEY);
    }

    // 当前使用vpn的应用包名
    public void saveUseVpnPackageName(String packageName){
        if (StringUtils.isEmpty(packageName)) {
            aCache.remove(KEY_CUR_USE_VPN_PACKAGE_NAME);
        }else {
            aCache.put(KEY_CUR_USE_VPN_PACKAGE_NAME, packageName);
        }
    }

    public String getUseVpnPackageName(){
        return aCache.getAsString(KEY_CUR_USE_VPN_PACKAGE_NAME);
    }


}
