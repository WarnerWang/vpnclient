package com.hxjg.vpn.ui.login;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hxjg.vpn.R;
import com.hxjg.vpn.api.bean.commonBean.user.UserInfo;
import com.hxjg.vpn.api.bean.req.user.GetSmsCodeReq;
import com.hxjg.vpn.api.bean.req.user.LoginByPhoneReq;
import com.hxjg.vpn.api.bean.resp.user.GetSmsCodeResp;
import com.hxjg.vpn.api.bean.resp.user.LoginByPhoneResp;
import com.hxjg.vpn.api.network.Api;
import com.hxjg.vpn.base.BaseActivity;
import com.hxjg.vpn.databinding.ActivityLoginCodeBinding;
import com.hxjg.vpn.eventbus.IEvent;
import com.hxjg.vpn.manager.UserManage;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.StringUtils;
import com.hxjg.vpn.utils.ToastUtil;
import com.hxjg.vpn.widge.WJTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginCodeActivity extends BaseActivity<ActivityLoginCodeBinding> {

    public static final String KEY_LOGIN_MOBILE = "loginMobile";

    EditText codeEdit;
    TextView resendBtn;
    WJTextView loginBtn;

    private String loginMobile;

    private Timer timer;
    private int time = 60;
    private final int settingTime = 60;
    private boolean timeCanRun = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        codeEdit = viewBinding.codeEdit;
        resendBtn = viewBinding.resendBtn;
        loginBtn = viewBinding.loginBtn;
        setStatusBarColor(R.color.backgroundColor);
        initBackBtn();
        Bundle extras = getIntent().getExtras();
        loginMobile = extras.getString(KEY_LOGIN_MOBILE);
        Logger.i("login mobile="+loginMobile);
        sendCode();
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCode();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    private void sendCode(){
        GetSmsCodeReq req = new GetSmsCodeReq();
        req.setPhone(loginMobile);
        Subscription subscription = Api.ins()
                .getUserAPI()
                .getSmsCode(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetSmsCodeResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i("err="+e.getMessage());
                        ToastUtil.ins().show(e.getMessage());
                    }

                    @Override
                    public void onNext(GetSmsCodeResp getSmsCodeResp) {
                        Logger.i("==== resp");
                        timerStart();
                    }
                });
        addSubscription(subscription);
    }

    private void timerStart() {
        if (timer != null) {
            return;
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (timer == null) {
                    return;
                }
                switch (msg.what) {
                    case 0: {
                        if (resendBtn == null) {
                            return;
                        }
                        resendBtn.setTextColor(Color.parseColor("#c0c0c0"));
                        resendBtn.setText(time + "??????????????????");
                    }
                    break;
                    case 1: {
                        if (resendBtn == null) {
                            return;
                        }
                        timer.cancel();
                        timer = null;
                        resendBtn.setText("????????????");
                        resendBtn.setTextColor(Color.parseColor("#0296F8"));
                    }
                    break;
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!timeCanRun) {
                    return;
                }
                if (time < 1) {
                    time = settingTime;
                    handler.sendEmptyMessage(1);
                    timeCanRun = false;
                } else {
                    handler.sendEmptyMessage(0);
                    time--;
                }
            }
        }, 0, 1000);
    }

    private void login(){
        String verificationCode = codeEdit.getText().toString();
        if (StringUtils.isEmpty(verificationCode)) {
            ToastUtil.ins().show("??????????????????");
            return;
        }
        LoginByPhoneReq req = new LoginByPhoneReq();
        req.setPhone(loginMobile);
        req.setVerificationCode(verificationCode);
        showProgressDialog();
        Subscription subscription = Api.ins()
                .getUserAPI()
                .loginByPhone(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginByPhoneResp>() {
                    @Override
                    public void onCompleted() {
                        dismissDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissDialog();
                        ToastUtil.ins().show(e.getMessage());
                    }

                    @Override
                    public void onNext(LoginByPhoneResp loginByPhoneResp) {

                        UserInfo userInfo = loginByPhoneResp.getUserInfo();
                        if (userInfo.getStatus() == 1) {
                            ToastUtil.ins().show("?????????????????????");
                            return;
                        }else if (userInfo.getStatus() == 2) {
                            ToastUtil.ins().show("?????????????????????");
                            return;
                        }
                        UserManage.ins().setSecretKey(loginByPhoneResp.getDynamicSecret());
                        UserManage.ins().setUserInfo(userInfo);
                        EventBus.getDefault().post(new IEvent.LoginSuccess());
                    }
                });
        addSubscription(subscription);
    }
}
