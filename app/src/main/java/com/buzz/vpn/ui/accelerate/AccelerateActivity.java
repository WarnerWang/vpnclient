package com.buzz.vpn.ui.accelerate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.buzz.vpn.Data;
import com.buzz.vpn.R;
import com.buzz.vpn.api.bean.base.BaseResp;
import com.buzz.vpn.api.bean.commonBean.app.AppInfo;
import com.buzz.vpn.api.bean.req.app.AccelerateApplyReq;
import com.buzz.vpn.api.bean.req.app.AccelerateDeleteReq;
import com.buzz.vpn.api.bean.resp.app.AccelerateApplyResp;
import com.buzz.vpn.api.image.ImageLoad;
import com.buzz.vpn.api.network.Api;
import com.buzz.vpn.common.LongRunningService;
import com.buzz.vpn.databinding.ActivityAccelerateBinding;
import com.buzz.vpn.manager.UserManage;
import com.buzz.vpn.ui.tabbar.TabBarActivity;
import com.buzz.vpn.utils.AppUtil;
import com.buzz.vpn.utils.Logger;
import com.buzz.vpn.utils.StringUtils;
import com.buzz.vpn.utils.ToastUtil;
import com.buzz.vpn.utils.TokenCache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.blinkt.openvpn.BindStatus;
import de.blinkt.openvpn.BindUtils;
import com.buzz.vpn.base.BaseVPNActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import de.blinkt.openvpn.StatusInfo;
import de.blinkt.openvpn.core.OpenVPNManagement;
import de.blinkt.openvpn.core.VpnStatus;
import de.blinkt.openvpn.core.TrafficHistory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static de.blinkt.openvpn.core.OpenVPNService.humanReadableByteCount;
import static java.lang.Math.max;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class AccelerateActivity extends BaseVPNActivity<ActivityAccelerateBinding> {
    public final static String KEY_ACCELERATE_APP_INFO = "KEY_ACCELERATE_APP_INFO";
    public final static String KEY_ACCELERATE_APP_NAME = "KEY_ACCELERATE_APP_NAME";
    public final static String KEY_ACCELERATE_APP_PACKAGE = "KEY_ACCELERATE_APP_PACKAGE";

    private static final int TIME_PERIOD_SECDONS = 0;
    private static final int TIME_PERIOD_MINUTES = 1;
    private static final int TIME_PERIOD_HOURS = 2;

    String data = "client\n" +
            "verb 4\n" +
            "connect-retry 2 300\n" +
            "resolv-retry 60\n" +
            "dev tun\n" +
            "remote 47.95.115.11 1194 tcp-client\n" +
            "<ca>\n" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDLzCCAhegAwIBAgIJAJyG9UMtuo+PMA0GCSqGSIb3DQEBCwUAMBQxEjAQBgNV\n" +
            "BAMMCW15b3BlbnZwbjAeFw0yMjAxMzAxODA0MTdaFw0zMjAxMjgxODA0MTdaMBQx\n" +
            "EjAQBgNVBAMMCW15b3BlbnZwbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
            "ggEBAKOUWVawXlgLxvyG6ttUzQoFCkHEdTxuU5VW/DpVq+m/PWNJS3z1KVeiGXOm\n" +
            "E2muFANTf3L5gV4oktsvlOwy0UltapDr7Z+G0os6Vp0Qj+/GkG423ZkKN29NL1W7\n" +
            "xgJuU+Ri5yK+u4QOyhtKnghooNrXUKIwLV5ZX5AA0R4pzhZieeU7WL5aTycjZnGX\n" +
            "xxmcGIOWN58k4oULxWQQ8/7liudPjwXBBld0Iu2B14cDQaBXFk8nrMxZYHicz7WB\n" +
            "HBJJxOtCq76LsFCwRJB5J4Q6VMBJGpL/qkArRNmn2ybi0ip1cYbV6NlvBF/z+UyP\n" +
            "pGVtKu8pSgZDGMeys21VBtGO0DUCAwEAAaOBgzCBgDAdBgNVHQ4EFgQU1U1R8HEm\n" +
            "I7ikEsyUDqchNumZ0KkwRAYDVR0jBD0wO4AU1U1R8HEmI7ikEsyUDqchNumZ0Kmh\n" +
            "GKQWMBQxEjAQBgNVBAMMCW15b3BlbnZwboIJAJyG9UMtuo+PMAwGA1UdEwQFMAMB\n" +
            "Af8wCwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQCdWMLR62nwgXPuDL8i\n" +
            "zwSe3mP1reoNihO2luK9iR/mT/TI4ucuDT7557DZ50SkE+Dq2g6YZcIBX/ZJgXgw\n" +
            "dDLtSzmAd/uivYLwVNAPBqsAcDKB1jIZ8hNuBacZYgmV/hPrJatwBVvg05Czi9ix\n" +
            "RiXmTmvBpByLsafgiBBob/RRfbqLxxmIMRkzrkM5WTFpaXKGKc2BjvoZ1eQAQAp2\n" +
            "LYssXJOWCl2njMeAIgJw4qZFeGFivo0MuxT0K/FP7wLok1fYt8OSHqrQDiROdror\n" +
            "B8EJxem3W9QyX2XLyW5laz/YcahY/S990+B/2iSFQ39mAl+t9xiAV582pfC7DqH4\n" +
            "mcSn\n" +
            "-----END CERTIFICATE-----\n" +
            "\n" +
            "</ca>\n" +
            "<key>\n" +
            "-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
            "MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIUDWn5lU8Z+8CAggA\n" +
            "MBQGCCqGSIb3DQMHBAjPY/Ff6lRDEASCBMgDSfinpGVKYvIJC2ZTPyq4VrHoHPie\n" +
            "fqMh2AgxuVJs8erdPhH7+jRt6me6T8qqvT7a3MqUHb69QKn4D5TH2FLT613cmkp8\n" +
            "qjSNUtTXmBJyUEyANaaCgbBFIUVOs69SH+osY9YSJFJ9TGbT0pTgcXtF4r2laLf4\n" +
            "6gbLaKrxeAewh5+Ym7KYdLUzHdpJw4DK+Crm1ji/LzP/LaOx9MvICPkRyFuGo8Ml\n" +
            "MeYbg9FRfLFexXj+0O6x9Wpomk0FGUUW0tFf/Z4+9i5Rlybk37yKlSNG90nlCwnW\n" +
            "MjuSf96VzSMyzVwPwequOPgUgPTYaO6ZaqERWS/TI8bPCVYOPsI/0j5lGC5Nl1cY\n" +
            "qulQRhppO0EMe3yQyPc2OW0bxfTGJS9UdcI0AspItrK9qtHk3W8o5HSUep4oUY7q\n" +
            "7GontsHKk1t6g4f+uppCDhk8reEaAiEEkGc117dzCjlOedqsmXxFS6ZM5rADMz7g\n" +
            "wxHjserG451JeJCiAVnxBuZdFLlGwL3i4SHmUVIe/uwWZ8kRRz6faFieda9BuJcj\n" +
            "f9KxExnqgz2pkUiCyvPwZBquTM8v7Oog2VPwHdduJ1JjTLdSkahm5j90YBwCGLLR\n" +
            "7QJj1l+NUPmGPTSf/uY/oK0SeHSfb0YoPDU1mNqOV3i3c/OzdX/kDMEkV6dWZHpk\n" +
            "S8EkhYDNRQfSz8xmP4dRS3aF6LTDtc3kg704FgvLE21LJYLoPdorCDEmBMoe6XC2\n" +
            "pGwgzijk2oEciqTkiwAAZV5WUF2q7dEYr2KaYzl2HJc/EyPwiq+Gw4jC9rw6BmzY\n" +
            "52i5QYOM5bhVxHScfnSeAWLSPFC56NPHzfqtxc+OLOwvDvsC/OIEhGQTmIEl7akV\n" +
            "vQ2Os570hJK87PUIkzCL4dnAFwvqjjT2NtfIdn9uOgfHGOeUeIO79heDUbvkDxS2\n" +
            "HReaVg9p+9UNc2aeMhy6h0kLZFQadUfmm/Flf1qGsRwco+yAYbQe57JFFscEjFy+\n" +
            "FFV+XAKqne6C7jiWwJAxRw99xtoM+1nerFFE4H71e1bnUTHphwLG7UG70L4KYqIq\n" +
            "IxLItmO5bmVMSWuoEM6LEdq/2j47NZ3j7W1bW1QjwyAV3WRLzzm7u2Pf33jxY6gN\n" +
            "H30V2Lg5r9/I5IiallGShDc7QELy34YefcUKdzOV+M+noatVdF1Fwb+lHrV5aJEn\n" +
            "LSmd2fVgKjDuNeMP7dM8skWVKMD2XnMe7Gd8Twaf1ZZ/ryuGUbUuoYrRqKwijhzO\n" +
            "siLELkvcXcpEwUB8hUgWFaQ8PTEi4O58k2rDZw4V8MOl8ThGjYWtgLv5kmjDe8Xg\n" +
            "4ggsiOdjCCk9TgCxpb0zQjpRLmgM69F0FxfugbviJ9a9ZZHZHkdwhkBiN+5w9x/L\n" +
            "NrUoE1Cz7dyVAzP4HTWHSR/TpsbPYmfOZ4UzhllAyPJJ6Ok9GRGnESL4TJjWSap/\n" +
            "H2e9sSMBMk1fWZq14TJXyrg07Wn38BPJNa8EOJwtthg+aeKBb3y10BpZCKNp/2Rj\n" +
            "qpmm6L6z4pHe4Jj4pg9lVN7u+ObudGcs3asiRbJnG2w6BFmTCQn6jLEMFgfO/V23\n" +
            "5GMFzyjP5XPJ3STkBqrRhdz746fiApoSCoQ7gNekfhZ0oAeABNr0iMazFmJlaUY1\n" +
            "/4s=\n" +
            "-----END ENCRYPTED PRIVATE KEY-----\n" +
            "\n" +
            "</key>\n" +
            "<cert>\n" +
            "Certificate:\n" +
            "    Data:\n" +
            "        Version: 3 (0x2)\n" +
            "        Serial Number:\n" +
            "            3a:5c:0b:41:26:92:95:92:9d:b3:ab:c4:78:80:99:62\n" +
            "    Signature Algorithm: sha256WithRSAEncryption\n" +
            "        Issuer: CN=myopenvpn\n" +
            "        Validity\n" +
            "            Not Before: Jan 30 18:24:54 2022 GMT\n" +
            "            Not After : May  4 18:24:54 2024 GMT\n" +
            "        Subject: CN=myclient\n" +
            "        Subject Public Key Info:\n" +
            "            Public Key Algorithm: rsaEncryption\n" +
            "                Public-Key: (2048 bit)\n" +
            "                Modulus:\n" +
            "                    00:cb:7d:5c:f9:f0:fe:23:69:10:87:ac:87:99:80:\n" +
            "                    65:1b:30:b0:ae:9b:fc:9b:9f:61:c6:b5:68:28:35:\n" +
            "                    12:6d:79:f1:d9:c0:d5:47:78:9b:a5:43:60:86:3e:\n" +
            "                    65:f3:36:cf:eb:50:bd:6e:9a:05:64:73:e7:15:27:\n" +
            "                    24:e0:d2:98:5b:e9:52:8d:44:ae:67:55:4a:1d:52:\n" +
            "                    f9:c9:0e:f5:e0:57:95:47:23:06:dd:34:6a:53:55:\n" +
            "                    a2:5c:d5:e0:51:49:b7:69:4a:a6:98:9d:0a:2a:df:\n" +
            "                    a3:ce:0a:2d:fe:09:3c:c9:99:8a:27:9d:05:b7:eb:\n" +
            "                    d4:f3:f1:ab:82:cb:4b:09:e7:a3:bb:56:ab:3e:40:\n" +
            "                    50:69:0c:2e:cd:62:70:72:8f:2b:0c:ec:68:dd:28:\n" +
            "                    a1:70:cf:05:fb:ab:f0:3c:46:2a:47:b1:86:4e:17:\n" +
            "                    64:53:12:2e:09:c7:9b:e5:25:09:38:1a:73:e0:57:\n" +
            "                    e2:67:e6:8b:8f:47:08:ab:62:e9:87:2a:c0:45:8d:\n" +
            "                    e0:7f:95:a7:eb:73:f2:a4:6b:f2:81:a5:6d:f1:0a:\n" +
            "                    36:1a:34:7f:fd:de:0c:47:25:7b:b7:5b:76:33:66:\n" +
            "                    4e:e6:f2:35:2f:3d:79:d1:ed:b4:f6:ca:e5:ba:e8:\n" +
            "                    fd:2b:3d:4c:a1:83:d4:8a:36:1a:9d:b8:7f:87:3b:\n" +
            "                    7c:47\n" +
            "                Exponent: 65537 (0x10001)\n" +
            "        X509v3 extensions:\n" +
            "            X509v3 Basic Constraints: \n" +
            "                CA:FALSE\n" +
            "            X509v3 Subject Key Identifier: \n" +
            "                51:7B:A6:F1:71:57:E0:92:3B:FE:CE:37:CF:BA:A0:72:7D:A2:11:74\n" +
            "            X509v3 Authority Key Identifier: \n" +
            "                keyid:D5:4D:51:F0:71:26:23:B8:A4:12:CC:94:0E:A7:21:36:E9:99:D0:A9\n" +
            "                DirName:/CN=myopenvpn\n" +
            "                serial:9C:86:F5:43:2D:BA:8F:8F\n" +
            "\n" +
            "            X509v3 Extended Key Usage: \n" +
            "                TLS Web Client Authentication\n" +
            "            X509v3 Key Usage: \n" +
            "                Digital Signature\n" +
            "    Signature Algorithm: sha256WithRSAEncryption\n" +
            "         90:73:db:38:bd:7f:36:6b:fc:13:4a:07:54:96:6c:39:50:20:\n" +
            "         50:1d:90:51:60:44:11:00:6e:69:c8:36:19:d1:d2:a0:8c:1a:\n" +
            "         af:3f:c7:cd:d2:b8:38:a5:79:e5:dc:80:f6:de:e2:f1:c4:af:\n" +
            "         db:ef:14:a5:90:a3:dd:5c:a6:f2:9f:ab:0b:f9:d9:65:a9:de:\n" +
            "         fe:3b:c6:a4:d9:1e:ac:71:bc:4c:1e:ff:a0:17:47:70:54:2e:\n" +
            "         5d:66:5c:48:26:1c:83:49:00:9e:2d:5e:a2:a4:f6:27:7e:67:\n" +
            "         68:97:c8:81:ed:a6:47:c9:7d:5c:71:9d:2c:78:69:35:72:d5:\n" +
            "         44:92:a1:fb:25:6b:a9:b3:2e:6d:6c:20:ff:8d:9c:36:f1:12:\n" +
            "         08:bb:b1:46:7a:47:c8:10:df:4c:52:b5:a9:2f:c7:b2:bd:84:\n" +
            "         8c:cf:16:24:c1:44:4f:b7:58:71:f3:a1:6a:9a:46:bd:14:63:\n" +
            "         69:34:da:69:d8:f4:50:03:e1:08:e9:fa:54:13:32:69:c1:62:\n" +
            "         79:a4:59:b1:e5:e7:15:7a:2e:c1:c4:9e:22:5f:09:5b:54:15:\n" +
            "         dc:5c:f8:ce:21:ea:4b:ac:58:05:ed:24:3f:24:9f:66:70:d6:\n" +
            "         fc:70:71:7c:14:4a:19:67:40:fb:6e:1c:ab:0e:f8:b2:d9:0a:\n" +
            "         41:e5:f9:ad\n" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDRzCCAi+gAwIBAgIQOlwLQSaSlZKds6vEeICZYjANBgkqhkiG9w0BAQsFADAU\n" +
            "MRIwEAYDVQQDDAlteW9wZW52cG4wHhcNMjIwMTMwMTgyNDU0WhcNMjQwNTA0MTgy\n" +
            "NDU0WjATMREwDwYDVQQDDAhteWNsaWVudDCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
            "ADCCAQoCggEBAMt9XPnw/iNpEIesh5mAZRswsK6b/JufYca1aCg1Em158dnA1Ud4\n" +
            "m6VDYIY+ZfM2z+tQvW6aBWRz5xUnJODSmFvpUo1ErmdVSh1S+ckO9eBXlUcjBt00\n" +
            "alNVolzV4FFJt2lKppidCirfo84KLf4JPMmZiiedBbfr1PPxq4LLSwnno7tWqz5A\n" +
            "UGkMLs1icHKPKwzsaN0ooXDPBfur8DxGKkexhk4XZFMSLgnHm+UlCTgac+BX4mfm\n" +
            "i49HCKti6YcqwEWN4H+Vp+tz8qRr8oGlbfEKNho0f/3eDEcle7dbdjNmTubyNS89\n" +
            "edHttPbK5bro/Ss9TKGD1Io2Gp24f4c7fEcCAwEAAaOBlTCBkjAJBgNVHRMEAjAA\n" +
            "MB0GA1UdDgQWBBRRe6bxcVfgkjv+zjfPuqByfaIRdDBEBgNVHSMEPTA7gBTVTVHw\n" +
            "cSYjuKQSzJQOpyE26ZnQqaEYpBYwFDESMBAGA1UEAwwJbXlvcGVudnBuggkAnIb1\n" +
            "Qy26j48wEwYDVR0lBAwwCgYIKwYBBQUHAwIwCwYDVR0PBAQDAgeAMA0GCSqGSIb3\n" +
            "DQEBCwUAA4IBAQCQc9s4vX82a/wTSgdUlmw5UCBQHZBRYEQRAG5pyDYZ0dKgjBqv\n" +
            "P8fN0rg4pXnl3ID23uLxxK/b7xSlkKPdXKbyn6sL+dllqd7+O8ak2R6scbxMHv+g\n" +
            "F0dwVC5dZlxIJhyDSQCeLV6ipPYnfmdol8iB7aZHyX1ccZ0seGk1ctVEkqH7JWup\n" +
            "sy5tbCD/jZw28RIIu7FGekfIEN9MUrWpL8eyvYSMzxYkwURPt1hx86Fqmka9FGNp\n" +
            "NNpp2PRQA+EI6fpUEzJpwWJ5pFmx5ecVei7BxJ4iXwlbVBXcXPjOIepLrFgF7SQ/\n" +
            "JJ9mcNb8cHF8FEoZZ0D7bhyrDviy2QpB5fmt\n" +
            "-----END CERTIFICATE-----\n" +
            "\n" +
            "</cert>\n" +
            "comp-lzo\n" +
            "persist-tun\n" +
            "preresolve\n" +
            "push-peer-info\n" +
            "management-query-proxy\n";

    private String TAG = "TAG";
//    private TextView textView;
//    private TextView appInfoTv;
    private TextView navRightBtn;

    private AppInfo appInfo;
    private String appName;
    private String packageName;
    private String appUsage;
    private ChartDataAdapter mChartAdapter;
    private int mColourIn;
    private int mColourOut;
    private int mColourPoint;
    private int mTextColour;
    private long firstTs;
    private boolean mLogScale = false;
    private boolean needHttp = true;

    private Handler mHandler;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, AccelerateActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
        if (VpnStatus.isVPNActive()) {
            viewBinding.accelerateTime.setText("00:00:00");
        }else {
            viewBinding.accelerateTime.setVisibility(View.INVISIBLE);
        }
        initBackBtn();
        setStatusBarColor(R.color.backgroundColor);
        BindUtils.bind(this);
        Intent intent = new Intent(this, LongRunningService.class);
        startService(intent);
        Bundle extras = getIntent().getExtras();
        navRightBtn = (TextView)findViewById(R.id.nav_right_btn);
        navRightBtn.setVisibility(View.VISIBLE);
        navRightBtn.setText("不调接口");
        navRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needHttp = !needHttp;
                if (needHttp) {
                    navRightBtn.setText("不调接口");
                }else {
                    navRightBtn.setText("调接口");
                }
            }
        });
        appName = extras.getString(KEY_ACCELERATE_APP_NAME);
        setTitle(appName);
        packageName = extras.getString(KEY_ACCELERATE_APP_PACKAGE);
        appInfo = (AppInfo) getIntent().getSerializableExtra(KEY_ACCELERATE_APP_INFO);
        if (appInfo != null) {
            if (appInfo.getAppDrawable() != null) {
                viewBinding.accelerateAppIcon.setImageDrawable(appInfo.getAppDrawable());
            }else {
                ImageLoad.loadImage(viewBinding.accelerateAppIcon.getContext(), appInfo.getApplicationIcon(),viewBinding.accelerateAppIcon);
            }
        }
        hasInternetConnection();
        viewBinding.accelerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // && !StringUtils.isEmpty(appInfo.getApplicationInfoId())
//                if (haveConnectedWifi) {
//                    ToastUtil.ins().show("当前连接的是WIFI，无法加速");
//                    return;
//                }
                if (needHttp) {
                    if (VpnStatus.isVPNActive()) {
                        accelerateDelete(false);
                    }else {
                        accelerateApply(false);
                    }
                }else {
                    if (VpnStatus.isVPNActive()) {
                        stop_vpn();
                    }else {
                        startActivityVpn();
                    }
                };

            }
        });
        viewBinding.accelerateBefore.setText("0");
        viewBinding.accelerateAfter.setText("0");
//        appInfoTv = (TextView) findViewById(R.id.tv_app_info);
//        appInfoTv.setText("当前允许加速APP为："+appName);
//        textView = (TextView) findViewById(R.id.tv_log);
//        textV = (TextView) findViewById(R.id.tv_running_status);

        mHandler = new Handler();
        mColourIn = getResources().getColor(R.color.line_in);
        mColourOut = getResources().getColor(R.color.line_out);
//        mColourPoint = getActivity().getResources().getColor(android.R.color.black);
        List<Integer> charts = new LinkedList<>();
        charts.add(TIME_PERIOD_SECDONS);
//        charts.add(TIME_PERIOD_MINUTES);
//        charts.add(TIME_PERIOD_HOURS);

        mChartAdapter = new ChartDataAdapter(this, charts);
        viewBinding.graphListview.setAdapter(mChartAdapter);
    }

    public void startActivityVpn(){
        loadVpnProfile(data);
        getVpnProfile().mName = "vpn";
        getVpnProfile().mKeyPassword = "huaitaosha";
        getVpnProfile().mAllowedAppsVpnAreDisallowed = false;
        HashSet<String> allowApp = new HashSet<>();
        allowApp.add(packageName);
        getVpnProfile().mAllowedAppsVpn=allowApp;
        startVpn();
    }

    private Runnable triggerRefresh = new Runnable() {
        @Override
        public void run() {
            mChartAdapter.notifyDataSetChanged();
            mHandler.postDelayed(triggerRefresh, OpenVPNManagement.mBytecountInterval * 1500);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(triggerRefresh, OpenVPNManagement.mBytecountInterval * 1500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(triggerRefresh);
    }

    private void updateStartBtn(){
        if (VpnStatus.isVPNActive()) {
            viewBinding.accelerateBtn.setText("停止加速");
            viewBinding.accelerateBtn.setSolidColor(R.color.transparent);
            viewBinding.accelerateBtn.setBorderColor(R.color.border_color);
        }else {
            viewBinding.accelerateBtn.setText("加速");
            viewBinding.accelerateBtn.setSolidColor(R.color.btn_color);
            viewBinding.accelerateBtn.setBorderColor(R.color.transparent);
        }
    }

    @BindStatus
    public void onStatus(final StatusInfo statusInfo) {
        Log.d(TAG, "--onStatus: " + statusInfo.toString());
        String text = "";
        switch (statusInfo.getLevel()) {
            case LEVEL_START:
                // 开始连接
                text = "开始连接";
                break;
            case LEVEL_CONNECTED:
                // 已连接
                text = "已连接";
                break;
            case LEVEL_VPNPAUSED:
                // 暂停
                text = "暂停";
                break;
            case LEVEL_NONETWORK:
                // 无网络
                text = "无网络";
                break;
            case LEVEL_CONNECTING_SERVER_REPLIED:
                // 服务器答应
                text = "服务器答应";
                break;
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
                // 服务器不答应
                text = "服务器无应答";
                break;
            case LEVEL_NOTCONNECTED:
                // 连接关闭
                text = "连接关闭";
                break;
            case LEVEL_AUTH_FAILED:
                // 认证失败
                text = "认证失败";
                break;
            case LEVEL_WAITING_FOR_USER_INPUT:
                // 等待用户输入
                text = "等待用户输入";
                break;
            case UNKNOWN_LEVEL:
                // 未知错误
                text = "未知错误";
                break;
        }
        final String finalText = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textView.setText("onStatus: "+ finalText + "-" + statusInfo.toString());
//                textV.setText("当前消耗流量："+appUsage);

                updateStartBtn();
//                VpnStatus.trafficHistory.getLastDiff()
            }
        });

    }

    @Override
    public void updateByteCount(long ins, long outs, long diffIn, long diffOut) {
        final long Total = ins + outs;
        String usage = "";
        if (Total < 1000) {
            usage = "1KB";
        } else if ((Total >= 1000) && (Total <= 1000_000)) {
            usage = (Total / 1000) + "KB";
        } else {
            usage = (Total / 1000_000) + "MB";
        }
        Logger.i("total usage = "+ Data.LongDataUsage);
        appUsage = usage;
        if (firstTs == 0)
            firstTs = System.currentTimeMillis() / 100;

        long now = (System.currentTimeMillis() / 100) - firstTs;
        int interval = OpenVPNManagement.mBytecountInterval * 10;
        Resources res = getResources();

        String in = humanReadableByteCount(ins, false, res);
        String out = humanReadableByteCount(outs, false, res);
        String diffInStr = humanReadableByteCount(diffIn / OpenVPNManagement.mBytecountInterval, true, res);
        String diffOutStr = humanReadableByteCount(diffOut / OpenVPNManagement.mBytecountInterval, true, res);
        final String netstat = String.format(getString(R.string.statusline_bytecount),
                in,
                diffInStr,
                out,
                diffOutStr);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textV.setText("当前消耗流量："+appUsage);
                mHandler.removeCallbacks(triggerRefresh);
                viewBinding.accelerateUsage.setText(appUsage);
                viewBinding.accelerateBefore.setText(in);
                viewBinding.accelerateAfter.setText(out);
                mChartAdapter.notifyDataSetChanged();
                mHandler.postDelayed(triggerRefresh, OpenVPNManagement.mBytecountInterval * 1500);
            }
        });
    }

    @Override
    public void onDestroy() {
        BindUtils.unBind(this);
        super.onDestroy();
    }

    @Override
    public Intent getJumpIntent() {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), TabBarActivity.class.getName());
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private void accelerateApply(boolean needReRequest){
        AccelerateApplyReq req = new AccelerateApplyReq();
        req.setApplicationInfoId(appInfo == null ? "1" : appInfo.getApplicationInfoId());
        req.setMobile(UserManage.ins().getUserInfo().getPhone());
        req.setPrivateIp(AppUtil.getLocalIPAddress());
        req.setPublicIp(TokenCache.getIns().getNetIpAddress());
//        req.setPrivateIp("10.96.197.247");
//        req.setPublicIp("112.224.163.230");
        List<String> targetIpList = new ArrayList<>();
        targetIpList.add("47.95.115.11");
//        targetIpList.add(appInfo.getTargetIp());
        req.setTargetIp(targetIpList);
        Subscription subscription = Api.ins()
                .getAppAPI()
                .accelerateApply(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccelerateApplyResp>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("refresh onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().contains("重复")) {
                            accelerateDelete(true);
                        }else {
                            ToastUtil.ins().show(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(AccelerateApplyResp resp) {
                        startActivityVpn();
                    }
                });
        addSubscription(subscription);
    }

    private void accelerateDelete(boolean needReRequest){
        AccelerateDeleteReq req = new AccelerateDeleteReq();
        req.setApplicationInfoId(appInfo == null ? "1" : appInfo.getApplicationInfoId());
        req.setApplyOrderId("");
        req.setMobile(UserManage.ins().getUserInfo().getPhone());
        Subscription subscription = Api.ins()
                .getAppAPI()
                .accelerateDelete(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResp>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("refresh onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.ins().show(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        if (needReRequest) {
                            accelerateApply(false);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private class ChartDataAdapter extends ArrayAdapter<Integer> {

        private Context mContext;

        public ChartDataAdapter(Context context, List<Integer> trafficData) {
            super(context, 0, trafficData);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.graph_item, parent, false);
                holder.chart = (LineChart) convertView.findViewById(R.id.chart);
                holder.title = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            // holder.chart.setValueTypeface(mTf);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);
            holder.chart.getLegend().setTextColor(mTextColour);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(mTextColour);

            switch (position) {
                case TIME_PERIOD_HOURS:
                    holder.title.setText(R.string.avghour);
                    break;
                case TIME_PERIOD_MINUTES:
                    holder.title.setText(R.string.avgmin);
                    break;
                default:
                    holder.title.setText(R.string.last5minutes);
                    break;
            }

            xAxis.setValueFormatter(new ValueFormatter() {


                @Override
                public String getFormattedValue(float value) {
                    switch (position) {
                        case TIME_PERIOD_HOURS:
                            return String.format(Locale.getDefault(), "%.0f\u2009h ago", (xAxis.getAxisMaximum() - value) / 10 / 3600);
                        case TIME_PERIOD_MINUTES:
                            return String.format(Locale.getDefault(), "%.0f\u2009m ago", (xAxis.getAxisMaximum() - value) / 10 / 60);
                        default:
                            return String.format(Locale.getDefault(), "%.0f\u2009s ago", (xAxis.getAxisMaximum() - value) / 10);
                    }

                }
            });
            xAxis.setLabelCount(5);

            YAxis yAxis = holder.chart.getAxisLeft();
            yAxis.setLabelCount(5, false);

            final Resources res = getResources();
            yAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if (mLogScale && value < 2.1f)
                        return "< 100\u2009bit/s";
                    if (mLogScale)
                        value = (float) Math.pow(10, value) / 8;

                    return humanReadableByteCount((long) value, true, res);
                }
            });
            yAxis.setTextColor(mTextColour);

            holder.chart.getAxisRight().setEnabled(false);

            LineData data = getDataSet(position);
            float ymax = data.getYMax();

            if (mLogScale) {
                yAxis.setAxisMinimum(2f);
                yAxis.setAxisMaximum((float) Math.ceil(ymax));
                yAxis.setLabelCount((int) (Math.ceil(ymax - 2f)));
            } else {
                yAxis.setAxisMinimum(0f);
                yAxis.resetAxisMaximum();
                yAxis.setLabelCount(6);
            }

            if (data.getDataSetByIndex(0).getEntryCount() < 3)
                holder.chart.setData(null);
            else
                holder.chart.setData(data);

            holder.chart.setNoDataText(getString(R.string.notenoughdata));

            holder.chart.invalidate();
            //holder.chart.animateX(750);

            return convertView;
        }

        private LineData getDataSet(int timeperiod) {

            LinkedList<Entry> dataIn = new LinkedList<>();
            LinkedList<Entry> dataOut = new LinkedList<>();

            long interval;
            long totalInterval;

            LinkedList<TrafficHistory.TrafficDatapoint> list;
            switch (timeperiod) {
                case TIME_PERIOD_HOURS:
                    list = VpnStatus.trafficHistory.getHours();
                    interval = TrafficHistory.TIME_PERIOD_HOURS;
                    totalInterval = 0;
                    break;
                case TIME_PERIOD_MINUTES:
                    list = VpnStatus.trafficHistory.getMinutes();
                    interval = TrafficHistory.TIME_PERIOD_MINTUES;
                    totalInterval = TrafficHistory.TIME_PERIOD_HOURS * TrafficHistory.PERIODS_TO_KEEP;
                    ;

                    break;
                default:
                    list = VpnStatus.trafficHistory.getSeconds();
                    interval = OpenVPNManagement.mBytecountInterval * 1000;
                    totalInterval = TrafficHistory.TIME_PERIOD_MINTUES * TrafficHistory.PERIODS_TO_KEEP;
                    break;
            }
            if (list.size() == 0) {
                list = TrafficHistory.getDummyList();
            }


            long lastts = 0;
            float zeroValue;
            if (mLogScale)
                zeroValue = 2;
            else
                zeroValue = 0;

            long now = System.currentTimeMillis();


            long firstTimestamp = 0;
            long lastBytecountOut = 0;
            long lastBytecountIn = 0;

            for (TrafficHistory.TrafficDatapoint tdp : list) {
                if (totalInterval != 0 && (now - tdp.timestamp) > totalInterval)
                    continue;

                if (firstTimestamp == 0) {
                    firstTimestamp = list.peek().timestamp;
                    lastBytecountIn = list.peek().in;
                    lastBytecountOut = list.peek().out;
                }

                float t = (tdp.timestamp - firstTimestamp) / 100f;

                float in = (tdp.in - lastBytecountIn) / (float) (interval / 1000);
                float out = (tdp.out - lastBytecountOut) / (float) (interval / 1000);

                lastBytecountIn = tdp.in;
                lastBytecountOut = tdp.out;

                if (mLogScale) {
                    in = max(2f, (float) Math.log10(in * 8));
                    out = max(2f, (float) Math.log10(out * 8));
                }

                if (lastts > 0 && (tdp.timestamp - lastts > 2 * interval)) {
                    dataIn.add(new Entry((lastts - firstTimestamp + interval) / 100f, zeroValue));
                    dataOut.add(new Entry((lastts - firstTimestamp + interval) / 100f, zeroValue));

                    dataIn.add(new Entry(t - interval / 100f, zeroValue));
                    dataOut.add(new Entry(t - interval / 100f, zeroValue));
                }

                lastts = tdp.timestamp;

                dataIn.add(new Entry(t, in));
                dataOut.add(new Entry(t, out));

            }
            if (lastts < now - interval) {

                if (now - lastts > 2 * interval * 1000) {
                    dataIn.add(new Entry((lastts - firstTimestamp + interval * 1000) / 100f, zeroValue));
                    dataOut.add(new Entry((lastts - firstTimestamp + interval * 1000) / 100f, zeroValue));
                }

                dataIn.add(new Entry((now - firstTimestamp) / 100, zeroValue));
                dataOut.add(new Entry((now - firstTimestamp) / 100, zeroValue));
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            LineDataSet indata = new LineDataSet(dataIn, getString(R.string.data_in));
            LineDataSet outdata = new LineDataSet(dataOut, getString(R.string.data_out));

            setLineDataAttributes(indata, mColourIn);
            setLineDataAttributes(outdata, mColourOut);

            dataSets.add(indata);
            dataSets.add(outdata);

            return new LineData(dataSets);
        }

        private void setLineDataAttributes(LineDataSet dataSet, int colour) {
            dataSet.setLineWidth(2);
            dataSet.setCircleRadius(1);
            dataSet.setDrawCircles(true);
//            dataSet.setCircleColor(mColourPoint);
            dataSet.setDrawFilled(true);
            dataSet.setFillAlpha(42);
            dataSet.setFillColor(colour);
            dataSet.setColor(colour);
            dataSet.setMode(LineDataSet.Mode.LINEAR);

            dataSet.setDrawValues(false);
            dataSet.setValueTextColor(mTextColour);

        }
    }

    private static class ViewHolder {
        LineChart chart;
        TextView title;
    }
}
