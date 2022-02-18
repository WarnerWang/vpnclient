package com.buzz.vpn.statusView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.buzz.vpn.R;
import com.buzz.vpn.base.BaseActivity;
import com.buzz.vpn.utils.ScreenUtils;
import com.buzz.vpn.utils.StringUtils;

public class MyStatusView extends StatusView {

    public onRetryClickLister retryClickLister;
    private Context mContext;
    private String mEmptyText;
    private String mErrorText;

    public MyStatusView(Context context) {
        super(context);
        this.mContext = context;
    }

    public static MyStatusView getInstance(Context context, onRetryClickLister retryClickLister) {
        return getInstance(context,"暂无数据",retryClickLister);
    }

    public static MyStatusView getInstance(Context context, String emptyText, onRetryClickLister retryClickLister) {
        MyStatusView statusView = new MyStatusView(context);
        statusView.setEmptyText(emptyText);
        statusView.setRetryClickLister(retryClickLister);
        return statusView;
    }

    @Override
    public int getRetryViewLayoutId() {
        return 0;
    }

    @Override
    public int getLoadingViewLayoutId() {
        return 0;
    }
    //R.layout.item_layout_member_view
    @Override
    public int getEmptyViewLayoutId() {
        return 0;
    }

    @Override
    public int getSettingViewLayoutId() {
        return 0;
    }

    @Override
    public int getNeedVipViewLayoutId() {
        return 0;
    }

    @Override
    public int getNeedSVipViewLayoutId() {
        return 0;
    }

    @Override
    public int getOddsSettingViewLayoutId() {
        return 0;
    }

    @Override
    public void initLoadingView() {


    }

    @Override
    public void initRetryView() {

//        mRetryView.findViewById(R.id.retry_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (retryClickLister != null) {
//                    retryClickLister.onRetryClick();
//                }
//            }
//        });
//
//        mRetryView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mContext instanceof Activity) {
//                    ((Activity)mContext).finish();
//                }
//            }
//        });
    }

    @Override
    public void initSettingView() {

    }

    @Override
    public void initEmptyView() {
        if (!StringUtils.isEmpty(mEmptyText)) {
            TextView textView = mEmptyView.findViewById(R.id.empty_text);
            textView.setText(mEmptyText);
        }
    }

    @Override
    public void initNeedVipView() {

    }

    @Override
    public void initNeedSVipView() {

//        ImageView imageView = mNeedSVipView.findViewById(R.id.item_layout_member_img);
//        imageView.setImageResource(R.mipmap.icon_empty_member_high);
//        TextView textView = mNeedSVipView.findViewById(R.id.item_layout_member_text);
//        textView.setText("当前功能只对至尊会员开放");
//        WJTextView wjTextView = mNeedSVipView.findViewById(R.id.item_layout_member_open_vip);
//        wjTextView.setText("立即开通至尊VIP");
//        wjTextView.setTextColor(Color.parseColor("#E7C794"));
//        wjTextView.setGradientColor(Color.parseColor("#28201B"),Color.parseColor("#4C4845"));
//        wjTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((mContext instanceof BaseActivity && UserManage.needBindPhone())) {
//                    ((BaseActivity)mContext).showBindPhoneDialog();
//                    return;
//                }
//                MemberActivity.startMe(mContext);
//            }
//        });
    }

    @Override
    public void initOddsSettingLayoutId() {
//        ImageView imageView = mOddsSettingView.findViewById(R.id.item_layout_member_img);
//        imageView.setImageResource(R.mipmap.odds_model_setting);
//        TextView textView = mOddsSettingView.findViewById(R.id.item_layout_member_text);
//        textView.setText("尊敬的至尊VIP用户，您还没有配置自定义"+textView.getContext().getString(R.string.replace_pan_pei)+"模型的参数，请先去配置再返回此页面查看比赛");
//        WJTextView wjTextView = mOddsSettingView.findViewById(R.id.item_layout_member_open_vip);
//        wjTextView.setText("前往配置");
//        wjTextView.setTextColor(Color.parseColor("#E7C794"));
//        wjTextView.setGradientColor(Color.parseColor("#28201B"),Color.parseColor("#4C4845"));
//        wjTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CustomModelActivity.startMe(mContext);
//            }
//        });
    }

    @Override
    public void startLoading() {
//        ImageView ivBall = mLoadingView.findViewById(R.id.iv_ball);
//        ivBall.clearAnimation();
//        TranslateAnimation translateAnimation = new TranslateAnimation(
//                ivBall.getX(),
//                ivBall.getX(),
//                ivBall.getY(),
//                ivBall.getY() - ScreenUtils.dpToPx(mContext, 50));
//        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//        translateAnimation.setDuration(600);
//        translateAnimation.setRepeatCount(Integer.MAX_VALUE);
//        translateAnimation.setRepeatMode(Animation.REVERSE);
//        translateAnimation.start();
//        ivBall.setAnimation(translateAnimation);

    }

    public MyStatusView setRetryClickLister(onRetryClickLister retryClickLister) {
        this.retryClickLister = retryClickLister;
        return this;
    }

    public MyStatusView setEmptyText(String emptyText) {
        mEmptyText = emptyText;
        return this;
    }

    public MyStatusView setErrorText(String errorText) {
        mErrorText = errorText;
        TextView textView = mRetryView.findViewById(R.id.error_msg);
        textView.setText(errorText);
        return this;
    }

    public interface onRetryClickLister {
        void onRetryClick();
    }


}
