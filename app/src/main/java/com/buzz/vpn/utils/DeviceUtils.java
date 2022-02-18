package com.buzz.vpn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class DeviceUtils {

    public static String getUniqueId(Context context){

        String id = TokenCache.getIns().getDeviceId();
        if (!StringUtils.isEmpty(id)) {
            return id;
        }
        try {
            String macAddress = getAllVersionMacAddress(context);
            if (!StringUtils.isEmpty(macAddress)) {
                try {
                    String md5MacAddress = toMD5(macAddress.toUpperCase());
                    TokenCache.getIns().saveDeviceId(md5MacAddress);
                    return md5MacAddress;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            if (!StringUtils.isEmpty(imei)) {
                try {
                    String md5Imei = toMD5(imei);
                    TokenCache.getIns().saveDeviceId(md5Imei);
                    return md5Imei;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!StringUtils.isEmpty(androidID)) {
                try {
                    String deviceId = toMD5(androidID);
                    TokenCache.getIns().saveDeviceId(deviceId);
                    return deviceId;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();

                }
            }
        }catch (Exception e) {
            Logger.printException(e);
        }

        return "";
    }

    /**
     * 获取mac地址，然后进行MD5加密
     * @param context
     * @return
     */
    public static String getMac(Context context){
        String savedMacAddress = TokenCache.getIns().getMacAddress();
        if (!StringUtils.isEmpty(savedMacAddress)) {
            return savedMacAddress;
        }
        try {
            String macAddress = getAllVersionMacAddress(context);
            if (!StringUtils.isEmpty(macAddress)) {
                TokenCache.getIns().saveMacAddress(macAddress);
                return macAddress;
            }
        }catch (Exception e) {
            Logger.printException(e);
        }

        return "";
    }

    public static String getAndroidId(Context context){
        String savedAdnroidId = TokenCache.getIns().getDeviceId();
        if (!StringUtils.isEmpty(savedAdnroidId)) {
            return savedAdnroidId;
        }
        try {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!StringUtils.isEmpty(androidID)) {
                TokenCache.getIns().saveDeviceId(androidID);
                return androidID;
            }
        }catch (Exception e) {
            Logger.printException(e);
        }

        return "";
    }

    public static String getImei(Context context){
        String savedImei = TokenCache.getIns().getImei();
        if (!StringUtils.isEmpty(savedImei)) {
            return savedImei;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            if (!StringUtils.isEmpty(imei)) {
                TokenCache.getIns().saveImei(imei);
                return imei;

            }
        }catch (Exception e) {
            Logger.printException(e);
        }

        return "";
    }

    public static String getUA(Context context) {
        String savedUA = TokenCache.getIns().getUA();
        if (!StringUtils.isEmpty(savedUA)) {
            return savedUA;
        }
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                String userAgentString = new WebView(context).getSettings().getUserAgentString();
                if (!StringUtils.isEmpty(userAgentString)) {
                    int index = userAgentString.indexOf("AppleWebKit");
                    userAgentString = userAgentString.substring(0,index);
                    TokenCache.getIns().saveUA(userAgentString);
                }
                return userAgentString;
            }

        }catch (Exception e) {
            Logger.printException(e);
        }
        return "";
    }

    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    public static String getAllVersionMacAddress(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return getMacAddressBefore6(context);
        }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getMacAddress(context);
        }else {
//            String mac0 = getMacAddress(context);
            String mac1 = getMacAddress();
//            String mac2 = getMachineHardwareAddress();
            return mac1;
        }
    }

    public static String getMacAddressBefore6(Context context){
        // 如果是6.0以下，直接通过wifimanager获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String macAddress0 = getMacAddress0(context);
            if (!StringUtils.isEmpty(macAddress0)) {
                return macAddress0;
            }
        }
        return "";
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {

        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("----->" + "NetInfoManager",
                        "getMacAddress:" + e.toString());
            }

        }
        return macSerial;
    }

    private static String getMacAddress0(Context context) {
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            try {
                wifiInfo = wifiMgr.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } catch (Exception e) {
                Logger.e("----->" + "NetInfoManager",
                        "getMacAddress0:" + e.toString());
            }

        }
        return "";

    }

    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private static boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            Logger.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:"
                    + "access wifi state is enabled");
            return true;
        } else
            return false;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }
    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     *
     */
    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
}
