package com.buzz.vpn.ui.splash;

import android.os.Bundle;

import com.buzz.vpn.base.BaseActivity;
import com.buzz.vpn.eventbus.IEvent;
import com.buzz.vpn.manager.UserManage;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReport.initCrashReport(getApplicationContext(), "b60eb8d955", true);
        if (!UserManage.isLoggedIn()) {
            EventBus.getDefault().post(new IEvent.Logout(true));
        }else {
            EventBus.getDefault().post(new IEvent.LoginSuccess());
        }
//        EventBus.getDefault().post(new IEvent.LoginSuccess());
    }
}
