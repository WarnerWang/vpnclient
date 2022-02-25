package com.hxjg.vpn;

public class Data {
    static String FileUsername;
    static String FilePassword;


    static boolean isAppDetails = false, isConnectionDetails = false;
    static String PREF_USAGE = "daily_usage";
    public static String StringCountDown;
    public static long LongDataUsage;

    public static int connectSecond;
    // vpn类型 0-openvpn 1- wireguard
    public static int vpnType = -1;

}
