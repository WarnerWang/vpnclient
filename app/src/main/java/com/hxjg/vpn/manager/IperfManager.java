package com.hxjg.vpn.manager;

import android.content.Context;


import com.hxjg.vpn.utils.Logger;
import com.synaptictools.iperf.IPerf;
import com.synaptictools.iperf.IPerfConfig;
import com.synaptictools.iperf.IPerfResult;

import java.io.File;

import de.blinkt.openvpn.core.App;

public class IperfManager {

    private static volatile IperfManager iperfManager;

    private OnIperfRequestResultListener onIperfRequestResultListener;

    public interface OnIperfRequestResultListener {
        void onSuccess();

        void onError(String e);

        void onUpdate(String s);

        void onResult(String result);
    }

    private IperfManager(Context context) {
        if (context == null) {
            return;
        }
    }

    public OnIperfRequestResultListener getOnIperfRequestResultListener() {
        return onIperfRequestResultListener;
    }

    public void setOnIperfRequestResultListener(OnIperfRequestResultListener onIperfRequestResultListener) {
        this.onIperfRequestResultListener = onIperfRequestResultListener;
    }

    public static IperfManager getIns() {
        if (iperfManager == null) {
            synchronized (IperfManager.class) {
                if (iperfManager == null) {
                    iperfManager = new IperfManager(App.ins());
                }
            }
        }
        return iperfManager;
    }

    public static void sendIperf(boolean down,OnIperfRequestResultListener listener){
        IperfManager.getIns().onIperfRequestResultListener = listener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                File stream =  new File(App.ins().getFilesDir(),"iperf3.XXXXXX");

//                IPerfConfig config = new IPerfConfig(
//                        "iperf.biznetnetworks.com",
//                        5001,
//                        stream.getPath(),10,2,true,false,false,true);

                IPerfConfig config = new IPerfConfig(
                        "39.105.52.99",
                        5001,
                        stream.getPath(),10,2,down,false,false,true);

                IPerf.INSTANCE.onClearResult();

                IPerf.INSTANCE.seCallBack(iPerfResultCallback -> {
                    iPerfResultCallback.error(e -> {
                        Logger.d("IPerf","error"+e);
                        if (IperfManager.getIns().onIperfRequestResultListener != null) {
                            IperfManager.getIns().onIperfRequestResultListener.onError(e+"");
                        }
                        return null;
                    });
                    iPerfResultCallback.success(() -> {
                        Logger.d("IPerf","success ");
                        if (IperfManager.getIns().onIperfRequestResultListener != null) {
                            IperfManager.getIns().onIperfRequestResultListener.onSuccess();
                        }
                        return null;
                    });
                    iPerfResultCallback.update(s -> {

                        Logger.d("IPerf update",s);
                        if (IperfManager.getIns().onIperfRequestResultListener != null) {
                            IperfManager.getIns().onIperfRequestResultListener.onUpdate(s+"");
                        }
                        return null;
                    });

                    return null;
                });

                IPerfResult<String> request = IPerf.INSTANCE.request(config);
                String result = request.toString();
                Logger.i("IperfManager request = "+ result);
                if (IperfManager.getIns().onIperfRequestResultListener != null) {
                    IperfManager.getIns().onIperfRequestResultListener.onResult(getResultBytes(result, down?"sender":"receive"));
                }
            }
        }).start();

    }

    public static String getResultBytes(String result, String type) {
        int lastIndex = result.lastIndexOf(type);
        if (lastIndex == -1) {
            return "0";
        }
        String subString = result.substring(0, lastIndex);
        int startIndex = subString.lastIndexOf("Bytes  ") + "Bytes  ".length();
        int endIndex = subString.lastIndexOf("/sec");
        if (startIndex == -1 || endIndex == -1) {
            return "0";
        }
        return subString.substring(startIndex, endIndex);
    }
}
