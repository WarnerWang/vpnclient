package com.hxjg.vpn.api.bean.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.hxjg.vpn.utils.JsonUtil;

public class BaseReq {

    public BaseReq() {
//        this.header = CommonHeaderHelper.genHeader();
    }

    /**
     * header : {"source":"string","ip":"string","xingeToken":"string","userId":"string"}
     */

    @JSONField(serialize = false)
    private HeaderBean header;

    private String userId;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class HeaderBean {
        /**
         * source : string
         * ip : string
         * xingeToken : string
         * userId : string
         */

        private String source;
        private String ip;
        private String xingeToken;
        private String userId;
        private String version;

        @ApiModelProperty("设备类型 1-iOS 2-android 3-PC")
        private int deviceType;

        @ApiModelProperty("浏览器类型 1-原生代码 2-内置浏览器 3-微信浏览器 4-小程序原生代码 5-小程序浏览器 6-其他浏览器")
        private int webViewType;

        @ApiModelProperty("下载来源 如:yingyongbao,360,xiaomi,huawei,wandoujia,channel360,yybtg, vivo, oppo, huawei, xiaomi,yybtg01,yybtg02,yybtg03,yybtg04,yybtg05, toutiao, google, anzhi,juliang")
        private String downloadSource;

        private String deviceId;

        @ApiModelProperty("设备型号")
        private String deviceVersion;

        @ApiModelProperty("应用名称  例如：菠菜汪")
        private String appName;

        @ApiModelProperty("应用包名")
        private String appPackageName;

        @ApiModelProperty("mac地址")
        private String mac;

        @ApiModelProperty("androidId")
        private String androidId;

        @ApiModelProperty("imei标识")
        private String imei;

        @ApiModelProperty("浏览器标识")
        private String ua;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getXingeToken() {
            return xingeToken;
        }

        public void setXingeToken(String xingeToken) {
            this.xingeToken = xingeToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public int getWebViewType() {
            return webViewType;
        }

        public void setWebViewType(int webViewType) {
            this.webViewType = webViewType;
        }

        public String getDownloadSource() {
            return downloadSource;
        }

        public void setDownloadSource(String downloadSource) {
            this.downloadSource = downloadSource;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceVersion() {
            return deviceVersion;
        }

        public void setDeviceVersion(String deviceVersion) {
            this.deviceVersion = deviceVersion;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppPackageName() {
            return appPackageName;
        }

        public void setAppPackageName(String appPackageName) {
            this.appPackageName = appPackageName;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getAndroidId() {
            return androidId;
        }

        public void setAndroidId(String androidId) {
            this.androidId = androidId;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getUa() {
            return ua;
        }

        public void setUa(String ua) {
            this.ua = ua;
        }
    }

    @Override
    public String toString() {
//        String string = JsonUtil.toJSONString(this);
//        Log.i("请求参数",string);
        return JsonUtil.toJSONString(this);
    }
}
