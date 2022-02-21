package com.buzz.vpn.utils;

import android.content.Context;
import android.net.TrafficStats;

import com.airbnb.lottie.animation.content.Content;

import de.blinkt.openvpn.core.App;

public class TrafficStatsManager {

    private static volatile TrafficStatsManager trafficStatsManager;

    private long lastTotalRxBytes = 0;
    private long lastTotalTxBytes = 0;
    private long lastTimeStamp = 0;

    private long downSpeed;
    private long upSpeed;

    private TrafficStatsManager(Context context) {
        if (context == null) {
            return;
        }

    }

    public static TrafficStatsManager getIns() {
        if (trafficStatsManager == null) {
            synchronized (TrafficStatsManager.class) {
                if (trafficStatsManager == null) {
                    trafficStatsManager = new TrafficStatsManager(App.ins());
                }
            }
        }
        return trafficStatsManager;
    }

    public void updateNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTotalTxBytes = getTotalTxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        downSpeed = nowTotalRxBytes - lastTotalRxBytes;
        upSpeed = nowTotalTxBytes - lastTotalTxBytes;
        //  long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        lastTotalTxBytes = nowTotalTxBytes;
    }

    public long getDownSpeed() {
        return downSpeed;
    }

    public long getUpSpeed() {
        return upSpeed;
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(App.ins().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
    }

    private long getTotalTxBytes() {
        return TrafficStats.getUidTxBytes(App.ins().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalTxBytes()/1024);//转为KB
    }
}
