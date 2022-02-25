package com.hxjg.vpn.statusView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hxjg.vpn.R;

import androidx.fragment.app.Fragment;

public class StatusLayout extends FrameLayout {

    private static final String TAG = "StatusLayout";
    private Context context;
    private View mLoadingView;// 正在加载界面
    private View mRetryView;// 返回数据错误
    private View mContentView;// 正常的内容页面
    private View mSettingView;// 一般是无网络状态,需要去设置
    private View mEmptyView;// 返回数据是0个
    private View mNeedVipView;//需要普通会员
    private View mNeedSVipView;//需要至尊会员
    private View mOddsSettingView;//指数模型设置
    private boolean isInit = false;
    private static int LOADING = 1;
    private static int RETRY = 2;
    private static int CONTENT = 3;
    private static int SETTING = 4;
    private static int EMPTY = 5;
    private static int NEEDVIP = 6;
    private static int NEEDSVIP = 7;
    private static int ODDSSETTING = 8;
    private LayoutParams layoutParams;
    private StatusView statusView;

    public static StatusLayout getInstance(View view, StatusView statusView) {
        return new StatusLayout.Builder().setContentView(view).setStatusView(statusView).build();
    }

    public static StatusLayout getInstance(Activity activity, StatusView statusView) {
        return new StatusLayout.Builder().setContentView(activity).setStatusView(statusView).build();
    }

    public static StatusLayout getInstance(Fragment fragment, StatusView statusView) {
        return new StatusLayout.Builder().setContentView(fragment).setStatusView(statusView).build();
    }

    public StatusLayout(Context context) {
        super(context);
        this.context = context;

    }

    public StatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public StatusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    private void initStatusLayout() {
        if (isInit) {
            return;
        }
        isInit = true;
        int countNum = getChildCount();
        if (countNum != 1) {
            return;
        }
        log(getChildAt(0).getClass().getName());
        mContentView = getChildAt(0);
        mContentView.setTag(R.id.tag_empty_add_type, CONTENT);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        setStatusView(new DefaultStatusView(context));
    }

    public StatusLayout setStatusView(StatusView mStatusView) {
        this.statusView = mStatusView;
        setLoadingView();
        setRetryView();
        setEmptyView();
        setSettingView();
        setNeedVipView();
        setNeedSVipView();
        setOddsSettingView();
        showContent();
        return this;
    }

    /**
     * 设置加载页面
     *
     * @return
     */
    public StatusLayout setLoadingView() {
        removeView(mLoadingView);
        mLoadingView = statusView.getLoadingView();
        if (mLoadingView != null) {
            mLoadingView.setTag(R.id.tag_empty_add_view, true);
            mLoadingView.setTag(R.id.tag_empty_add_type, LOADING);
            addView(mLoadingView, layoutParams);
            mLoadingView.setVisibility(View.GONE);
        }
        return this;

    }


    /**
     * 设置重试界面
     *
     * @return
     */
    public StatusLayout setRetryView() {
        removeView(mRetryView);
        mRetryView = statusView.getRetryView();
        if (mRetryView != null) {
            mRetryView.setTag(R.id.tag_empty_add_view, true);
            mRetryView.setTag(R.id.tag_empty_add_type, RETRY);
            addView(mRetryView, layoutParams);
            mRetryView.setVisibility(View.GONE);
        }
        return this;

    }


    /**
     * 设置空的页面
     *
     * @return
     */
    public StatusLayout setEmptyView() {
        removeView(mEmptyView);
        mEmptyView = statusView.getEmptyView();
        if (mEmptyView != null) {
            mEmptyView.setTag(R.id.tag_empty_add_view, true);
            mEmptyView.setTag(R.id.tag_empty_add_type, EMPTY);
            addView(mEmptyView, layoutParams);
            mEmptyView.setVisibility(View.GONE);
        }
        return this;
    }


    /**
     * 设置设置网络界面
     *
     * @return
     */
    public StatusLayout setSettingView() {
        removeView(mSettingView);
        mSettingView = statusView.getSettingView();
        if (mSettingView != null) {
            mSettingView.setTag(R.id.tag_empty_add_view, true);
            mSettingView.setTag(R.id.tag_empty_add_type, SETTING);
            addView(mSettingView, layoutParams);
            mSettingView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置需要普通会员资格
     *
     * @return
     */
    public StatusLayout setNeedVipView() {
        removeView(mNeedVipView);
        mNeedVipView = statusView.getNeedVipView();
        if (mNeedVipView != null) {
            mNeedVipView.setTag(R.id.tag_empty_add_view, true);
            mNeedVipView.setTag(R.id.tag_empty_add_type, NEEDVIP);
            addView(mNeedVipView, layoutParams);
            mNeedVipView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置需要至尊会员资格
     *
     * @return
     */
    public StatusLayout setNeedSVipView() {
        removeView(mNeedSVipView);
        mNeedSVipView = statusView.getNeedSVipView();
        if (mNeedSVipView != null) {
            mNeedSVipView.setTag(R.id.tag_empty_add_view, true);
            mNeedSVipView.setTag(R.id.tag_empty_add_type, NEEDSVIP);
            addView(mNeedSVipView, layoutParams);
            mNeedSVipView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 指数设置提示页面
     * @return
     */
    public StatusLayout setOddsSettingView(){
        removeView(mOddsSettingView);
        mOddsSettingView = statusView.getOddsSettingView();
        if (mOddsSettingView != null) {
            mOddsSettingView.setTag(R.id.tag_empty_add_view, true);
            mOddsSettingView.setTag(R.id.tag_empty_add_type, ODDSSETTING);
            addView(mOddsSettingView, layoutParams);
            mOddsSettingView.setVisibility(View.GONE);
        }
        return this;
    }


    public View getmLoadingView() {
        return mLoadingView;
    }

    public View getmRetryView() {
        return mRetryView;
    }

    public View getmContentView() {
        return mContentView;
    }

    public View getmSettingView() {
        return mSettingView;
    }

    public View getmEmptyView() {
        return mEmptyView;
    }

    public View getmNeedVipView() {
        return mNeedVipView;
    }

    public View getmNeedSVipView() {
        return mNeedSVipView;
    }

    public View getmOddsSettingView(){
        return mOddsSettingView;
    }

    @Override
    public void addView(View child, int index,
                        ViewGroup.LayoutParams params) {
        boolean isAddEmpty = false;
        if (child.getTag(R.id.tag_empty_add_view) != null) {
            isAddEmpty = (Boolean) child.getTag(R.id.tag_empty_add_view);
        }
        if (!isAddEmpty) {
            if (getChildCount() > 0) {
                throw new IllegalStateException(
                        "StatusLayout can host only one direct child");
            }
        }
        super.addView(child, index, params);
        initStatusLayout();
    }

    private void log(String string) {
        Log.e(TAG, string);
    }

    public void showLoading() {
        log("showLoading");
        show(LOADING);
//        statusView.showLoading();

    }

    public void showRetry() {
        log("showRetry");
        show(RETRY);
    }

    public void showContent() {
        log("showContent");
        show(CONTENT);
    }

    public void showEmpty() {
        log("showEmpty");
        show(EMPTY);
    }

    public void showSetting() {
        log("showSetting");
        show(SETTING);
    }

    public void showNeedVip(){
        log("showNeedVip");
        show(NEEDVIP);
    }

    public void showNeedSVip(){
        log("showNeedSVip");
        show(NEEDSVIP);
    }

    public void showOddsSetting(){
        log("showOddsSetting");
        show(ODDSSETTING);
    }

    private void show(int type) {
        for (int i = 0; i < getChildCount(); i++) {
            int mType;
            if (getChildAt(i).getTag(R.id.tag_empty_add_type) != null) {
                mType = (Integer) getChildAt(i).getTag(R.id.tag_empty_add_type);
                if (type == mType) {
                    getChildAt(i).setVisibility(VISIBLE);
                } else {
                    getChildAt(i).setVisibility(GONE);
                }
            }
        }

    }

    public static final class Builder {
        private int index;
        private ViewGroup mParentView;
        private View mContentView;// 正常的内容页面
        private StatusLayout statusLayout;
        private boolean isRegister = false;
        private StatusView statusView;

        public Builder setContentView(Activity activity) {
            mParentView = (ViewGroup) activity
                    .findViewById(android.R.id.content);
            index = 0;
            return this;
        }

        public Builder setContentView(Fragment fragment) {
            mParentView = (ViewGroup) fragment.getView().getParent();
            index = 0;
            return this;
        }

        public Builder setContentView(View view) {
            mParentView = (ViewGroup) view.getParent();
            for (int i = 0; i < mParentView.getChildCount(); i++) {
                if (mParentView.getChildAt(i) == view) {
                    index = i;
                    break;
                }
            }
            return this;
        }

        public StatusLayout build() {
            View view = mParentView.getChildAt(index);
            // 父类本分就是StatusLayout
            if (mParentView != null && mParentView instanceof StatusLayout) {
                statusLayout = (StatusLayout) mParentView;
            }
            // 内容本分就是StatusLayout
            else if (view != null && view instanceof StatusLayout) {
                statusLayout = (StatusLayout) view;
            }
            // 其他情况
            else {
                mContentView = view;
                statusLayout = new StatusLayout(
                        mParentView.getContext());
                register();

            }
            if (statusView != null) {
                statusLayout.setStatusView(statusView);
            }

            return statusLayout;
        }


        public Builder setStatusView(StatusView statusView) {
            this.statusView = statusView;
            return this;
        }

        /**
         * @描述：只需要注册一次
         */
        public void register() {
            Log.d(TAG, "register: 添加布局");
            if (isRegister) {
                return;
            }
            if (mParentView == null || mContentView == null || statusLayout == null) {
                return;
            }
            mParentView.removeView(mContentView);
            ViewGroup.LayoutParams lp = mContentView.getLayoutParams();
            statusLayout.addView(mContentView);
            mParentView.addView(statusLayout, index, lp);
            isRegister = true;
        }

        /**
         * @描述：无所谓的调用
         */
//        public void unRegister() {
//            if (isRegister) {
//                if (mParentView == null || mContentView == null
//                        || statusLayout == null) {
//                    return;
//                }
//                mParentView.setTag(R.id.tag_empty_layout_manager, null);
//                mParentView.removeView(statusLayout);
//                ViewGroup.LayoutParams lp = statusLayout.getLayoutParams();
//                mParentView.addView(mContentView, index, lp);
//                mContentView.setVisibility(View.VISIBLE);
//            }
//
//        }
    }
}
