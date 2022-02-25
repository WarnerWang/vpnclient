package com.hxjg.vpn.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.hxjg.vpn.R;
import com.hxjg.vpn.base.BaseActivity;
import com.hxjg.vpn.databinding.ActivityWebViewBinding;
import com.hxjg.vpn.utils.Logger;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;


public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {
    private static final String KEY_URL = "URL";
    private static final String KEY_FROM_AD = "FROM_AD";
    LinearLayout root;
    private AgentWeb mAgentWeb;
    private JsInjectObj jsInjectObj;

    private boolean fromAd;

    public static void startMe(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        if (context instanceof Activity) {

        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void startMe(Context context, String url, boolean fromAd) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_FROM_AD, fromAd);
        if (context instanceof Activity) {

        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = viewBinding.root;
        setStatusBarWhite();
        setStatusBarDarkFont(true);
        initBackBtn();
        viewBinding.ibToolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String url = getIntent().getStringExtra(KEY_URL);
        fromAd = getIntent().getBooleanExtra(KEY_FROM_AD, false);
        url = getWebUrl(url);
        Logger.i("finally url:" + url);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(root, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setAgentWebWebSettings(getSettings())
//                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
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
        jsInjectObj = new JsInjectObj(this);
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", jsInjectObj);
        // window.android.xxx(); // js code
//        mAgentWeb.getJsInterfaceHolder().addJavaObject("shareAction",)
        // 告诉WebView启用JavaScript执行。默认的是false。
//        webSettings.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
//        webSettings.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
//        webSettings.setDomStorageEnabled(true);
        // 排版适应屏幕
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

    }

    private String getWebUrl(String url){

        if (url.contains("www.88lot.com") || url.contains("hexinjingu")) {
//            Uri.Builder builder = Uri.parse(url).buildUpon();
//            String userId = UserManage.ins().getUserId();
//            if (userId == null) {
//                userId = "";
//            }
//            builder.appendQueryParameter("userId", userId);
//            String appToken = TokenCache.getIns().getAppToken();
//            if (appToken == null) {
//                appToken = "";
//            }
//            builder.appendQueryParameter("token", appToken);
//            builder.appendQueryParameter("source", "2");
//            String secretKey = UserManage.ins().getSecretKey();
//            if (secretKey == null) {
//                secretKey = "";
//            }
//            builder.appendQueryParameter("secret", secretKey);
//            builder.appendQueryParameter("version", AppUtil.getVersionName());
//            builder.appendQueryParameter("timestamp", String.valueOf(Math.random()));
//            url = builder.build().toString();
        }
        return url;
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (fromAd) {
//            IEvent.LoginSuccess loginEvent = new IEvent.LoginSuccess();
//            EventBus.getDefault().post(loginEvent);
//            return true;
//        }
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onBackBtnClick() {
//        if (fromAd) {
//            IEvent.LoginSuccess event = new IEvent.LoginSuccess();
//            EventBus.getDefault().post(event);
//            return;
//        }
        if (mAgentWeb.handleKeyEvent(KeyEvent.KEYCODE_BACK, null)) {
            Logger.i("web view handle event");
        } else {
            super.onBackBtnClick();
        }
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //  super.onProgressChanged(view, newProgress);
            Logger.i("onProgressChanged:" + newProgress + "  view:" + view);
//            Logger.i("url : "+view.getUrl());
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
        }
    };
}
