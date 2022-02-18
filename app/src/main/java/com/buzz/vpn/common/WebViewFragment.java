package com.buzz.vpn.common;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.buzz.vpn.R;
import com.buzz.vpn.base.BaseFragment;
import com.buzz.vpn.databinding.ActivityWebViewBinding;
import com.buzz.vpn.utils.Logger;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author: 王杰
 * @描述:
 * @文件名: WebViewFragment
 * @包名: com.hx.sports.ui.common
 * @创建时间: 2019-09-04 10:44
 * @修改人: 王杰
 * @公司: 北京和信金谷科技有限公司
 * @备注:
 * @版本号: 1.0.0
 */
public class WebViewFragment extends BaseFragment<ActivityWebViewBinding> {

    private static final String KEY_URL = "URL";

    RelativeLayout rlToolbar;
    LinearLayout root;

    private View contentView;
    public AgentWeb mAgentWeb;
    private JsInjectObj jsInjectObj;

    public static WebViewFragment ins(String url){
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        contentView = view;
        rlToolbar = viewBinding.rlToolbar;
        root = viewBinding.root;
        rlToolbar.setVisibility(View.GONE);
        initWebView();
        //监听back必须设置的
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(backlistener);
        return view;
    }

    private void initWebView(){
        String url = getArguments().getString(KEY_URL);
        Uri.Builder builder = Uri.parse(url).buildUpon();
//        builder.appendQueryParameter("userId", UserManage.ins().getUserId());
//        builder.appendQueryParameter("token", TokenCache.getIns().getAppToken());
//        builder.appendQueryParameter("source", "2");
//        builder.appendQueryParameter("secret", UserManage.ins().getSecretKey());
//        builder.appendQueryParameter("version", AppUtil.getVersionName());
//        builder.appendQueryParameter("timestamp", String.valueOf(Math.random()));
        url = builder.build().toString();
        Logger.i("finally url:" + url);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(root, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)
//                .setWebChromeClient(mWebChromeClient)
//                .setPermissionInterceptor(mPermissionInterceptor)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//                .setAgentWebUIController(new UIController(getActivity()))
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
//                .useMiddlewareWebChrome(getMiddlewareWebChrome())
//                .useMiddlewareWebClient(getMiddlewareWebClient())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);

        IAgentWebSettings agentWebSettings = mAgentWeb.getAgentWebSettings();
        WebSettings webSettings = agentWebSettings.getWebSettings();
        webSettings.setUseWideViewPort(true);
        jsInjectObj = new JsInjectObj(getActivity());
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", jsInjectObj);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (contentView != null) {
//            contentView.setFocusable(true);
//            contentView.setFocusableInTouchMode(true);
//        }
    }

    @Override
    public void onResume() {
//        contentView.setFocusable(true);
//        contentView.setFocusableInTouchMode(true);
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }



    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
//        contentView.setFocusable(false);
//        contentView.setFocusableInTouchMode(false);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }

    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                //这边判断,如果是back的按键被点击了   就自己拦截实现掉
                if (mAgentWeb.handleKeyEvent(keyEvent.getKeyCode(), keyEvent)) {
                    return true;
                }
            }
            return false;
        }
    };

    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // 判断方法1 ,注释掉方法2再测试
            String url = view.getUrl();
            Logger.i("webView url is "+ url);
            if (url.contains("wx.tenpay.com")) {
                Logger.e("网页微信支付");
            }
            return super.shouldOverrideUrlLoading(view, request);
        }
    };
}
