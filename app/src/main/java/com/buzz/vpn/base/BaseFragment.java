package com.buzz.vpn.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.buzz.vpn.api.exception.ServerError;
import com.buzz.vpn.statusView.MyStatusView;
import com.buzz.vpn.statusView.StatusLayout;
import com.buzz.vpn.utils.Logger;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import rx.Subscription;

public class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T viewBinding;

    private MaterialDialog progressDialog;
    private MaterialDialog bindPhoneDialog;
    private ArrayList<Subscription> subscriptions = new ArrayList<>();

    private boolean isVisibleToUser;

    protected StatusLayout statusLayout;
    protected MyStatusView statusView;

    private OnRequestPermissionResultListener onRequestPermissionResultListener;

    public interface OnRequestPermissionResultListener {
        void onRequestPermissionResultListener(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            viewBinding = (T) inflate.invoke(null, inflater, container, false);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return viewBinding.getRoot();
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new MaterialDialog.Builder(getContext())
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }

        progressDialog.show();
    }

    protected void dismissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.cancel();
            }
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    public void initStatusView(View contentView, MyStatusView.onRetryClickLister onRetryClickLister){
        statusView = MyStatusView.getInstance(getActivity(), onRetryClickLister);
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

    public void showEmpty(){
        statusLayout.showEmpty();
    }

    protected void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void onDestroyView() {
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

        EventBus eventBus = EventBus.getDefault();
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
        dismissDialog();
        super.onDestroyView();
    }

    protected int getViewIdByString(String idStr, Context context) {
        int id = context.getResources().getIdentifier(idStr, "id", context.getPackageName());
        return id;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }

    /**
     * activity返回销毁刷新
     */
    public void onResultRefresh(int requestCode, int resultCode, Intent data) {

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
            if (ContextCompat.checkSelfPermission(getContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() == 0) {
            return true;
        }

        String[] needPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
        ActivityCompat.requestPermissions(getActivity(), needPermissions, resultCode);

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
