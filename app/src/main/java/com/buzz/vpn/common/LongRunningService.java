package com.buzz.vpn.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.buzz.vpn.utils.DateUtil;

import java.io.FileOutputStream;
import java.util.Date;

public class LongRunningService extends Service {
    private static final String TAG = "LongRunningService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "executed at " + DateUtil.dateToString(new Date());
                Log.d(TAG, msg);
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput("info.txt", Context.MODE_APPEND);
                    outputStream.write(msg.getBytes("utf-8"));
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmTime = 10 * 1000; // 定时10s
        long trigerAtTime = SystemClock.elapsedRealtime() + alarmTime;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }
}
