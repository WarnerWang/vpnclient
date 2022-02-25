package com.hxjg.vpn.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hxjg.vpn.R;
import com.hxjg.vpn.api.exception.ServerError;
import com.hxjg.vpn.eventbus.IEvent;
import com.hxjg.vpn.statusView.MyStatusView;
import com.hxjg.vpn.statusView.StatusLayout;
import com.hxjg.vpn.ui.login.LoginActivity;
import com.hxjg.vpn.ui.tabbar.TabBarActivity;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.ScreenUtils;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;
import rx.Subscription;

public class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    protected T viewBinding;

    private ImmersionBar mImmersionBar;
    private MaterialDialog progressDialog;
    private ArrayList<Subscription> subscriptions = new ArrayList<>();
    private OnRequestPermissionResultListener onRequestPermissionResultListener;

    public static final int REQUEST_CODE_FOR_DIR = 300;

    protected StatusLayout statusLayout;
    protected MyStatusView statusView;

    public interface OnRequestPermissionResultListener {
        void onRequestPermissionResultListener(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    public static void startMe(Context context, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
        ((Activity) context).startActivity(intent);
    }

    public static void startMe(Context context, Class activityClass, Bundle bundle) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtras(bundle);
        ((Activity) context).startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericSuperclass;
            Class cls = (Class) type.getActualTypeArguments()[0];
            try {
                Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
                viewBinding = (T) inflate.invoke(null, getLayoutInflater());
                setContentView(viewBinding.getRoot());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().register(this);
    }

    protected void transparentStatusBar() {
        //在BaseActivity里初始化
        transparentStatusBar(false);
    }

    protected void transparentStatusBar(boolean trans) {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarAlpha(trans ? 0f : 1.0f).init();
    }

    protected void setStatusBarWhite() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorWhite)
                .init();
    }

    protected void setStatusBarColor(@ColorRes int color) {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(color)
                .init();
    }

    protected void setStatusBarDarkFont(boolean dark) {
        if (mImmersionBar != null) {
            mImmersionBar.statusBarDarkFont(dark).init();
        }
    }


    protected View createFooterView(int height) {
        LinearLayout view = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPxInt(this, height));
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new MaterialDialog.Builder(this)
                    .backgroundColor(R.color.backgroundColor)
                    .contentGravity(GravityEnum.CENTER)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }

        progressDialog.show();
    }

    public void dismissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.cancel();
            }
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    public void initStatusView(View contentView, MyStatusView.onRetryClickLister onRetryClickLister){
        statusView = MyStatusView.getInstance(this, onRetryClickLister);
        statusLayout = new StatusLayout.Builder().setContentView(contentView).setStatusView(statusView).build();
        statusLayout.showLoading();
        statusView.startLoading();
    }

    public void showLoading(){
        statusLayout.showLoading();
    }

    public void showRetry(){
        statusLayout.showRetry();
        statusView.setErrorText("网络数据出错，请检查您的网络");
    }

    public void showRetry(ServerError error){
        statusLayout.showRetry();
        statusView.setErrorText("网络请求出错，错误码："+error.getErrorCode()+"，错误信息："+error.getMsg());
    }

    public void showNeedVip(){
        statusLayout.showNeedVip();
    }

    public void showNeedSVip(){
        statusLayout.showNeedSVip();
    }

    public void showContent(){
        statusLayout.showContent();
    }

    protected int getViewIdByString(String idStr, Context context) {
        int id = context.getResources().getIdentifier(idStr, "id", context.getPackageName());
        return id;
    }

    protected void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        try {
            for (Subscription subscription : subscriptions) {
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
            subscriptions.clear();
        } catch (Exception e) {
            Logger.printException(e);
        }

        dismissDialog();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    protected void setTitle(String title) {
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        if (null != tvTitle) {
            tvTitle.setText(title);
        }
    }

    protected void initBackBtn() {
        ImageButton backBtn = findViewById(R.id.ib_toolbar_back);
        if (null == backBtn) {
            Logger.w("back btn is null");
            return;
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackBtnClick();
            }
        });
    }

    protected void onBackBtnClick() {
        finish();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IEvent.Logout event) {
        Logger.d("this class type:" + this.toString());
        if (event.reLogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        this.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IEvent.LoginSuccess event) {
        Logger.i("LoginSuccess Event");
        TabBarActivity.startMe(this, 0);
        finish();
    }


    /**
     * 请求权限
     *
     * @param permissions 权限列表
     * @param resultCode
     * @return 是否已经具备相应权限
     */
    public boolean checkReadPermission(String[] permissions, int resultCode) {

        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() == 0) {
            return true;
        }

        String[] needPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
        ActivityCompat.requestPermissions(this, needPermissions, resultCode);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onRequestPermissionResultListener != null) {
            this.onRequestPermissionResultListener.onRequestPermissionResultListener(requestCode, permissions, grantResults);
        }
    }

    public void setOnRequestPermissionResultListener(OnRequestPermissionResultListener onRequestPermissionResultListener) {
        this.onRequestPermissionResultListener = onRequestPermissionResultListener;
    }
}
