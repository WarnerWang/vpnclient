package com.buzz.vpn;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import de.blinkt.openvpn.core.App;


public class WelcomeActivity extends AppCompatActivity {
    TextView tv_welcome_status, tv_welcome_app;
    String StringGetAppURL, StringGetConnectionURL;
    String AppDetails = "{\n" +
            "  \"ads\":\"false\",\n" +
            "  \"update\":[{\n" +
            "    \"version\":\"2.8.1600\",\n" +
            "    \"title\":\"This app is now Open Source\",\n" +
            "    \"description\":\"The App is available at github.com/gayankuruppu/android-vpn-client-ics-openvpn\",\n" +
            "    \"size\":\"https://git.io/JeY69\"\n" +
            "  }],\n" +
            "  \"blocked\":[\n" +
            "    {\"id\":0, \"app\":\"com.android.game\"}\n" +
            "  ],\n" +
            "  \"free\":[\n" +
            "    {\"id\":0, \"file\":0, \"city\":\"Beijing\",\"country\":\"China\",\"image\":\"germany\",\"ip\":\"47.95.115.11\",\"active\":\"true\",\"signal\":\"a\"}\n" +
            "  ]\n" +
            "}";
    String FileDetails = "{\n" +
            "  \"ovpn_file\":[\n" +
            "    {\"id\":0,\"file\":\"client\n" +
            "            \"verb 4\n" +
            "            \"connect-retry 2 300\n" +
            "            \"resolv-retry 60\n" +
            "            \"dev tun\n" +
            "            \"remote 47.95.115.11 1194 tcp-client\n" +
            "            \"<ca>\n" +
            "            \"-----BEGIN CERTIFICATE-----\n" +
            "            \"MIIDLzCCAhegAwIBAgIJAJyG9UMtuo+PMA0GCSqGSIb3DQEBCwUAMBQxEjAQBgNV\n" +
            "            \"BAMMCW15b3BlbnZwbjAeFw0yMjAxMzAxODA0MTdaFw0zMjAxMjgxODA0MTdaMBQx\n" +
            "            \"EjAQBgNVBAMMCW15b3BlbnZwbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
            "            \"ggEBAKOUWVawXlgLxvyG6ttUzQoFCkHEdTxuU5VW/DpVq+m/PWNJS3z1KVeiGXOm\n" +
            "            \"E2muFANTf3L5gV4oktsvlOwy0UltapDr7Z+G0os6Vp0Qj+/GkG423ZkKN29NL1W7\n" +
            "            \"xgJuU+Ri5yK+u4QOyhtKnghooNrXUKIwLV5ZX5AA0R4pzhZieeU7WL5aTycjZnGX\n" +
            "            \"xxmcGIOWN58k4oULxWQQ8/7liudPjwXBBld0Iu2B14cDQaBXFk8nrMxZYHicz7WB\n" +
            "            \"HBJJxOtCq76LsFCwRJB5J4Q6VMBJGpL/qkArRNmn2ybi0ip1cYbV6NlvBF/z+UyP\n" +
            "            \"pGVtKu8pSgZDGMeys21VBtGO0DUCAwEAAaOBgzCBgDAdBgNVHQ4EFgQU1U1R8HEm\n" +
            "            \"I7ikEsyUDqchNumZ0KkwRAYDVR0jBD0wO4AU1U1R8HEmI7ikEsyUDqchNumZ0Kmh\n" +
            "            \"GKQWMBQxEjAQBgNVBAMMCW15b3BlbnZwboIJAJyG9UMtuo+PMAwGA1UdEwQFMAMB\n" +
            "            \"Af8wCwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQCdWMLR62nwgXPuDL8i\n" +
            "            \"zwSe3mP1reoNihO2luK9iR/mT/TI4ucuDT7557DZ50SkE+Dq2g6YZcIBX/ZJgXgw\n" +
            "            \"dDLtSzmAd/uivYLwVNAPBqsAcDKB1jIZ8hNuBacZYgmV/hPrJatwBVvg05Czi9ix\n" +
            "            \"RiXmTmvBpByLsafgiBBob/RRfbqLxxmIMRkzrkM5WTFpaXKGKc2BjvoZ1eQAQAp2\n" +
            "            \"LYssXJOWCl2njMeAIgJw4qZFeGFivo0MuxT0K/FP7wLok1fYt8OSHqrQDiROdror\n" +
            "            \"B8EJxem3W9QyX2XLyW5laz/YcahY/S990+B/2iSFQ39mAl+t9xiAV582pfC7DqH4\n" +
            "            \"mcSn\n" +
            "            \"-----END CERTIFICATE-----\n" +
            "            \"\n" +
            "            \"</ca>\n" +
            "            \"<key>\n" +
            "            \"-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
            "            \"MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIUDWn5lU8Z+8CAggA\n" +
            "            \"MBQGCCqGSIb3DQMHBAjPY/Ff6lRDEASCBMgDSfinpGVKYvIJC2ZTPyq4VrHoHPie\n" +
            "            \"fqMh2AgxuVJs8erdPhH7+jRt6me6T8qqvT7a3MqUHb69QKn4D5TH2FLT613cmkp8\n" +
            "            \"qjSNUtTXmBJyUEyANaaCgbBFIUVOs69SH+osY9YSJFJ9TGbT0pTgcXtF4r2laLf4\n" +
            "            \"6gbLaKrxeAewh5+Ym7KYdLUzHdpJw4DK+Crm1ji/LzP/LaOx9MvICPkRyFuGo8Ml\n" +
            "            \"MeYbg9FRfLFexXj+0O6x9Wpomk0FGUUW0tFf/Z4+9i5Rlybk37yKlSNG90nlCwnW\n" +
            "            \"MjuSf96VzSMyzVwPwequOPgUgPTYaO6ZaqERWS/TI8bPCVYOPsI/0j5lGC5Nl1cY\n" +
            "            \"qulQRhppO0EMe3yQyPc2OW0bxfTGJS9UdcI0AspItrK9qtHk3W8o5HSUep4oUY7q\n" +
            "            \"7GontsHKk1t6g4f+uppCDhk8reEaAiEEkGc117dzCjlOedqsmXxFS6ZM5rADMz7g\n" +
            "            \"wxHjserG451JeJCiAVnxBuZdFLlGwL3i4SHmUVIe/uwWZ8kRRz6faFieda9BuJcj\n" +
            "            \"f9KxExnqgz2pkUiCyvPwZBquTM8v7Oog2VPwHdduJ1JjTLdSkahm5j90YBwCGLLR\n" +
            "            \"7QJj1l+NUPmGPTSf/uY/oK0SeHSfb0YoPDU1mNqOV3i3c/OzdX/kDMEkV6dWZHpk\n" +
            "            \"S8EkhYDNRQfSz8xmP4dRS3aF6LTDtc3kg704FgvLE21LJYLoPdorCDEmBMoe6XC2\n" +
            "            \"pGwgzijk2oEciqTkiwAAZV5WUF2q7dEYr2KaYzl2HJc/EyPwiq+Gw4jC9rw6BmzY\n" +
            "            \"52i5QYOM5bhVxHScfnSeAWLSPFC56NPHzfqtxc+OLOwvDvsC/OIEhGQTmIEl7akV\n" +
            "            \"vQ2Os570hJK87PUIkzCL4dnAFwvqjjT2NtfIdn9uOgfHGOeUeIO79heDUbvkDxS2\n" +
            "            \"HReaVg9p+9UNc2aeMhy6h0kLZFQadUfmm/Flf1qGsRwco+yAYbQe57JFFscEjFy+\n" +
            "            \"FFV+XAKqne6C7jiWwJAxRw99xtoM+1nerFFE4H71e1bnUTHphwLG7UG70L4KYqIq\n" +
            "            \"IxLItmO5bmVMSWuoEM6LEdq/2j47NZ3j7W1bW1QjwyAV3WRLzzm7u2Pf33jxY6gN\n" +
            "            \"H30V2Lg5r9/I5IiallGShDc7QELy34YefcUKdzOV+M+noatVdF1Fwb+lHrV5aJEn\n" +
            "            \"LSmd2fVgKjDuNeMP7dM8skWVKMD2XnMe7Gd8Twaf1ZZ/ryuGUbUuoYrRqKwijhzO\n" +
            "            \"siLELkvcXcpEwUB8hUgWFaQ8PTEi4O58k2rDZw4V8MOl8ThGjYWtgLv5kmjDe8Xg\n" +
            "            \"4ggsiOdjCCk9TgCxpb0zQjpRLmgM69F0FxfugbviJ9a9ZZHZHkdwhkBiN+5w9x/L\n" +
            "            \"NrUoE1Cz7dyVAzP4HTWHSR/TpsbPYmfOZ4UzhllAyPJJ6Ok9GRGnESL4TJjWSap/\n" +
            "            \"H2e9sSMBMk1fWZq14TJXyrg07Wn38BPJNa8EOJwtthg+aeKBb3y10BpZCKNp/2Rj\n" +
            "            \"qpmm6L6z4pHe4Jj4pg9lVN7u+ObudGcs3asiRbJnG2w6BFmTCQn6jLEMFgfO/V23\n" +
            "            \"5GMFzyjP5XPJ3STkBqrRhdz746fiApoSCoQ7gNekfhZ0oAeABNr0iMazFmJlaUY1\n" +
            "            \"/4s=\n" +
            "            \"-----END ENCRYPTED PRIVATE KEY-----\n" +
            "            \"\n" +
            "            \"</key>\n" +
            "            \"<cert>\n" +
            "            \"Certificate:\n" +
            "            \"    Data:\n" +
            "            \"        Version: 3 (0x2)\n" +
            "            \"        Serial Number:\n" +
            "            \"            3a:5c:0b:41:26:92:95:92:9d:b3:ab:c4:78:80:99:62\n" +
            "            \"    Signature Algorithm: sha256WithRSAEncryption\n" +
            "            \"        Issuer: CN=myopenvpn\n" +
            "            \"        Validity\n" +
            "            \"            Not Before: Jan 30 18:24:54 2022 GMT\n" +
            "            \"            Not After : May  4 18:24:54 2024 GMT\n" +
            "            \"        Subject: CN=myclient\n" +
            "            \"        Subject Public Key Info:\n" +
            "            \"            Public Key Algorithm: rsaEncryption\n" +
            "            \"                Public-Key: (2048 bit)\n" +
            "            \"                Modulus:\n" +
            "            \"                    00:cb:7d:5c:f9:f0:fe:23:69:10:87:ac:87:99:80:\n" +
            "            \"                    65:1b:30:b0:ae:9b:fc:9b:9f:61:c6:b5:68:28:35:\n" +
            "            \"                    12:6d:79:f1:d9:c0:d5:47:78:9b:a5:43:60:86:3e:\n" +
            "            \"                    65:f3:36:cf:eb:50:bd:6e:9a:05:64:73:e7:15:27:\n" +
            "            \"                    24:e0:d2:98:5b:e9:52:8d:44:ae:67:55:4a:1d:52:\n" +
            "            \"                    f9:c9:0e:f5:e0:57:95:47:23:06:dd:34:6a:53:55:\n" +
            "            \"                    a2:5c:d5:e0:51:49:b7:69:4a:a6:98:9d:0a:2a:df:\n" +
            "            \"                    a3:ce:0a:2d:fe:09:3c:c9:99:8a:27:9d:05:b7:eb:\n" +
            "            \"                    d4:f3:f1:ab:82:cb:4b:09:e7:a3:bb:56:ab:3e:40:\n" +
            "            \"                    50:69:0c:2e:cd:62:70:72:8f:2b:0c:ec:68:dd:28:\n" +
            "            \"                    a1:70:cf:05:fb:ab:f0:3c:46:2a:47:b1:86:4e:17:\n" +
            "            \"                    64:53:12:2e:09:c7:9b:e5:25:09:38:1a:73:e0:57:\n" +
            "            \"                    e2:67:e6:8b:8f:47:08:ab:62:e9:87:2a:c0:45:8d:\n" +
            "            \"                    e0:7f:95:a7:eb:73:f2:a4:6b:f2:81:a5:6d:f1:0a:\n" +
            "            \"                    36:1a:34:7f:fd:de:0c:47:25:7b:b7:5b:76:33:66:\n" +
            "            \"                    4e:e6:f2:35:2f:3d:79:d1:ed:b4:f6:ca:e5:ba:e8:\n" +
            "            \"                    fd:2b:3d:4c:a1:83:d4:8a:36:1a:9d:b8:7f:87:3b:\n" +
            "            \"                    7c:47\n" +
            "            \"                Exponent: 65537 (0x10001)\n" +
            "            \"        X509v3 extensions:\n" +
            "            \"            X509v3 Basic Constraints: \n" +
            "            \"                CA:FALSE\n" +
            "            \"            X509v3 Subject Key Identifier: \n" +
            "            \"                51:7B:A6:F1:71:57:E0:92:3B:FE:CE:37:CF:BA:A0:72:7D:A2:11:74\n" +
            "            \"            X509v3 Authority Key Identifier: \n" +
            "            \"                keyid:D5:4D:51:F0:71:26:23:B8:A4:12:CC:94:0E:A7:21:36:E9:99:D0:A9\n" +
            "            \"                DirName:/CN=myopenvpn\n" +
            "            \"                serial:9C:86:F5:43:2D:BA:8F:8F\n" +
            "            \"\n" +
            "            \"            X509v3 Extended Key Usage: \n" +
            "            \"                TLS Web Client Authentication\n" +
            "            \"            X509v3 Key Usage: \n" +
            "            \"                Digital Signature\n" +
            "            \"    Signature Algorithm: sha256WithRSAEncryption\n" +
            "            \"         90:73:db:38:bd:7f:36:6b:fc:13:4a:07:54:96:6c:39:50:20:\n" +
            "            \"         50:1d:90:51:60:44:11:00:6e:69:c8:36:19:d1:d2:a0:8c:1a:\n" +
            "            \"         af:3f:c7:cd:d2:b8:38:a5:79:e5:dc:80:f6:de:e2:f1:c4:af:\n" +
            "            \"         db:ef:14:a5:90:a3:dd:5c:a6:f2:9f:ab:0b:f9:d9:65:a9:de:\n" +
            "            \"         fe:3b:c6:a4:d9:1e:ac:71:bc:4c:1e:ff:a0:17:47:70:54:2e:\n" +
            "            \"         5d:66:5c:48:26:1c:83:49:00:9e:2d:5e:a2:a4:f6:27:7e:67:\n" +
            "            \"         68:97:c8:81:ed:a6:47:c9:7d:5c:71:9d:2c:78:69:35:72:d5:\n" +
            "            \"         44:92:a1:fb:25:6b:a9:b3:2e:6d:6c:20:ff:8d:9c:36:f1:12:\n" +
            "            \"         08:bb:b1:46:7a:47:c8:10:df:4c:52:b5:a9:2f:c7:b2:bd:84:\n" +
            "            \"         8c:cf:16:24:c1:44:4f:b7:58:71:f3:a1:6a:9a:46:bd:14:63:\n" +
            "            \"         69:34:da:69:d8:f4:50:03:e1:08:e9:fa:54:13:32:69:c1:62:\n" +
            "            \"         79:a4:59:b1:e5:e7:15:7a:2e:c1:c4:9e:22:5f:09:5b:54:15:\n" +
            "            \"         dc:5c:f8:ce:21:ea:4b:ac:58:05:ed:24:3f:24:9f:66:70:d6:\n" +
            "            \"         fc:70:71:7c:14:4a:19:67:40:fb:6e:1c:ab:0e:f8:b2:d9:0a:\n" +
            "            \"         41:e5:f9:ad\n" +
            "            \"-----BEGIN CERTIFICATE-----\n" +
            "            \"MIIDRzCCAi+gAwIBAgIQOlwLQSaSlZKds6vEeICZYjANBgkqhkiG9w0BAQsFADAU\n" +
            "            \"MRIwEAYDVQQDDAlteW9wZW52cG4wHhcNMjIwMTMwMTgyNDU0WhcNMjQwNTA0MTgy\n" +
            "            \"NDU0WjATMREwDwYDVQQDDAhteWNsaWVudDCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
            "            \"ADCCAQoCggEBAMt9XPnw/iNpEIesh5mAZRswsK6b/JufYca1aCg1Em158dnA1Ud4\n" +
            "            \"m6VDYIY+ZfM2z+tQvW6aBWRz5xUnJODSmFvpUo1ErmdVSh1S+ckO9eBXlUcjBt00\n" +
            "            \"alNVolzV4FFJt2lKppidCirfo84KLf4JPMmZiiedBbfr1PPxq4LLSwnno7tWqz5A\n" +
            "            \"UGkMLs1icHKPKwzsaN0ooXDPBfur8DxGKkexhk4XZFMSLgnHm+UlCTgac+BX4mfm\n" +
            "            \"i49HCKti6YcqwEWN4H+Vp+tz8qRr8oGlbfEKNho0f/3eDEcle7dbdjNmTubyNS89\n" +
            "            \"edHttPbK5bro/Ss9TKGD1Io2Gp24f4c7fEcCAwEAAaOBlTCBkjAJBgNVHRMEAjAA\n" +
            "            \"MB0GA1UdDgQWBBRRe6bxcVfgkjv+zjfPuqByfaIRdDBEBgNVHSMEPTA7gBTVTVHw\n" +
            "            \"cSYjuKQSzJQOpyE26ZnQqaEYpBYwFDESMBAGA1UEAwwJbXlvcGVudnBuggkAnIb1\n" +
            "            \"Qy26j48wEwYDVR0lBAwwCgYIKwYBBQUHAwIwCwYDVR0PBAQDAgeAMA0GCSqGSIb3\n" +
            "            \"DQEBCwUAA4IBAQCQc9s4vX82a/wTSgdUlmw5UCBQHZBRYEQRAG5pyDYZ0dKgjBqv\n" +
            "            \"P8fN0rg4pXnl3ID23uLxxK/b7xSlkKPdXKbyn6sL+dllqd7+O8ak2R6scbxMHv+g\n" +
            "            \"F0dwVC5dZlxIJhyDSQCeLV6ipPYnfmdol8iB7aZHyX1ccZ0seGk1ctVEkqH7JWup\n" +
            "            \"sy5tbCD/jZw28RIIu7FGekfIEN9MUrWpL8eyvYSMzxYkwURPt1hx86Fqmka9FGNp\n" +
            "            \"NNpp2PRQA+EI6fpUEzJpwWJ5pFmx5ecVei7BxJ4iXwlbVBXcXPjOIepLrFgF7SQ/\n" +
            "            \"JJ9mcNb8cHF8FEoZZ0D7bhyrDviy2QpB5fmt\n" +
            "            \"-----END CERTIFICATE-----\n" +
            "            \"\n" +
            "            \"</cert>\n" +
            "            \"comp-lzo\n" +
            "            \"persist-tun\n" +
            "            \"preresolve\n" +
            "            \"push-peer-info\n" +
            "            \"management-query-proxy\\n\"},\n" +
            "    {\"id\":1,\"file\":\"\"}]\n" +
            "}";
//    String FileDetails = "";
    SharedPreferences SharedAppDetails;
    TextView tv_welcome_title, tv_welcome_description, tv_welcome_size, tv_welcome_version;

    int Random;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        StringGetAppURL = "https://raw.githubusercontent.com/gayanvoice/android-vpn-client-ics-openvpn/images/appdetails.json";
        StringGetConnectionURL = "https://raw.githubusercontent.com/gayanvoice/android-vpn-client-ics-openvpn/images/filedetails.json";
        //StringGetConnectionURL = "https://gayankuruppu.github.io/buzz/connection.html";

        Typeface RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface RobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface RobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        tv_welcome_status = findViewById(R.id.tv_welcome_status);
        tv_welcome_app = findViewById(R.id.tv_welcome_app);
        tv_welcome_title = findViewById(R.id.tv_welcome_title);
        tv_welcome_description = findViewById(R.id.tv_welcome_description);
        tv_welcome_size = findViewById(R.id.tv_welcome_size);
        tv_welcome_version = findViewById(R.id.tv_welcome_version);

        tv_welcome_title.setTypeface(RobotoMedium);
        tv_welcome_description.setTypeface(RobotoRegular);
        tv_welcome_size.setTypeface(RobotoMedium);
        tv_welcome_version.setTypeface(RobotoMedium);


        startAnimation(WelcomeActivity.this, R.id.ll_welcome_loading, R.anim.slide_up_800, true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation(WelcomeActivity.this, R.id.ll_welcome_details, R.anim.slide_up_800, true);
            }
        }, 1000);


        tv_welcome_status.setTypeface(RobotoMedium);
        tv_welcome_app.setTypeface(RobotoBold);

        Button btn_welcome_update = findViewById(R.id.btn_welcome_update);
        Button btn_welcome_later = findViewById(R.id.btn_welcome_later);

        btn_welcome_update.setTypeface(RobotoMedium);
        btn_welcome_later.setTypeface(RobotoMedium);

        btn_welcome_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gayanvoice/android-vpn-client-ics-openvpn")));
                    /*
                    The following lines of code load the PlayStore

                    Bundle params = new Bundle();
                    params.putString("device_id", App.device_id);
                    params.putString("click", "play");
                    mFirebaseAnalytics.logEvent("app_param_click", params);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.buzz.vpn"));
                    startActivity(intent);
                    */
                } catch (ActivityNotFoundException activityNotFound) {
                    // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.buzz.vpn")));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gayanvoice/android-vpn-client-ics-openvpn")));

                } catch (Exception e) {
                    Bundle params = new Bundle();
                    params.putString("device_id", App.device_id);
                    params.putString("exception", "WA1" + e.toString());
                    mFirebaseAnalytics.logEvent("app_param_error", params);
                }
            }

        });

        btn_welcome_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);

            }

        });

        if (!Data.isConnectionDetails) {
            if (!Data.isAppDetails) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAppDetails();
                    }
                }, 2000);
            }
        }

    }

    void getAppDetails() {
        tv_welcome_status.setText("GETTING APP DETAILS");
        Data.isAppDetails = true;
//        RequestQueue queue = Volley.newRequestQueue(WelcomeActivity.this);
//        queue.getCache().clear();
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, StringGetAppURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String Response) {
//                        Log.e("Response", Response);
//                        AppDetails = Response;
//                        Data.isAppDetails = true;
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Bundle params = new Bundle();
//                params.putString("device_id", App.device_id);
//                params.putString("exception", "WA2" + error.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
//
//                Data.isAppDetails = false;
//            }
//        });
//
//        queue.add(stringRequest);
//        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
//            @Override
//            public void onRequestFinished(Request<String> request) {
//                if (Data.isAppDetails) {
//                    getFileDetails();
//                } else {
//                    tv_welcome_status.setText("CONNECTION INTERRUPTED");
//                }
//            }
//        });
        getFileDetails();
    }

    void getFileDetails() {
        tv_welcome_status.setText("GETTING CONNECTION DETAILS");
        Data.isConnectionDetails = true;
//        RequestQueue queue = Volley.newRequestQueue(WelcomeActivity.this);
//        queue.getCache().clear();
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, StringGetConnectionURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String Response) {
//                        FileDetails = Response;
//                        Data.isConnectionDetails = true;
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Bundle params = new Bundle();
//                params.putString("device_id", App.device_id);
//                params.putString("exception", "WA3" + error.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
//
//                Data.isConnectionDetails = false;
//            }
//        });
//        queue.add(stringRequest);
//        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
//            @Override
//            public void onRequestFinished(Request<String> request) {
//
//                loadFinished();
//            }
//        });
        loadFinished();
    }

    void loadFinished(){
        final int min = 0;
        final int max = 4;
        Random = new Random().nextInt((max - min) + 1) + min;

        String Ads = "NULL", cuVersion = "NULL", upVersion = "NULL", upTitle = "NULL", upDescription = "NULL", upSize = "NULL";
        String ID = "NULL", FileID = "NULL", File = "NULL", City = "NULL", Country = "NULL", Image = "NULL",
                IP = "NULL", Active = "NULL", Signal = "NULL";
        String BlockedApps = "NULL";

        try {
            JSONObject jsonResponse = new JSONObject(AppDetails);
            Ads = jsonResponse.getString("ads");
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA4" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        try {
            JSONObject jsonResponse = new JSONObject(AppDetails);
            JSONArray jsonArray = jsonResponse.getJSONArray("update");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            upVersion = jsonObject.getString("version");
            upTitle = jsonObject.getString("title");
            upDescription = jsonObject.getString("description");
            upSize = jsonObject.getString("size");
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA5" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        try {
            JSONObject json_response = new JSONObject(AppDetails);
            JSONArray jsonArray = json_response.getJSONArray("free");
            JSONObject json_object = jsonArray.getJSONObject(Random);
            ID = json_object.getString("id");
            FileID = json_object.getString("file");
            City = json_object.getString("city");
            Country = json_object.getString("country");
            Image = json_object.getString("image");
            IP = json_object.getString("ip");
            Active = json_object.getString("active");
            Signal = json_object.getString("signal");
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA5" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        try {
            JSONObject json_response = new JSONObject(FileDetails);
            JSONArray jsonArray = json_response.getJSONArray("ovpn_file");
            JSONObject json_object = jsonArray.getJSONObject(Integer.valueOf(FileID));
            FileID = json_object.getString("id");
            File = json_object.getString("file");
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA6" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }


        // save details
        EncryptData En = new EncryptData();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            cuVersion = pInfo.versionName;
            if (cuVersion.isEmpty()) {
                cuVersion = "0.0.0";
            }

            SharedAppDetails = getSharedPreferences("app_details", 0);
            SharedPreferences.Editor Editor = SharedAppDetails.edit();
            Editor.putString("ads", Ads);
            Editor.putString("up_title", upTitle);
            Editor.putString("up_description", upDescription);
            Editor.putString("up_size", upSize);
            Editor.putString("up_version", upVersion);
            Editor.putString("cu_version", cuVersion);
            Editor.apply();
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA7" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        try {
            SharedAppDetails = getSharedPreferences("connection_data", 0);
            SharedPreferences.Editor Editor = SharedAppDetails.edit();
            Editor.putString("id", ID);
            Editor.putString("file_id", FileID);
            Editor.putString("file", En.encrypt(File));
            Editor.putString("city", City);
            Editor.putString("country", Country);
            Editor.putString("image", Image);
            Editor.putString("ip", IP);
            Editor.putString("active", Active);
            Editor.putString("signal", Signal);
            Editor.apply();
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA8" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        try {
            SharedAppDetails = getSharedPreferences("app_values", 0);
            SharedPreferences.Editor Editor = SharedAppDetails.edit();
            Editor.putString("app_details", En.encrypt(AppDetails));
            Editor.putString("file_details", En.encrypt(FileDetails));
            Editor.apply();
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "WA9" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        tv_welcome_title.setText(upTitle);
        tv_welcome_description.setText(upDescription);
        tv_welcome_size.setText(upSize);
        tv_welcome_version.setText(upVersion);

        if (Data.isConnectionDetails) {
            if (cuVersion.equals(upVersion)) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            } else {
                startAnimation(WelcomeActivity.this, R.id.ll_welcome_loading, R.anim.fade_out_500, false);
                Handler handlerData = new Handler();
                handlerData.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(WelcomeActivity.this, R.id.ll_update_details, R.anim.slide_up_800, true);
                    }
                }, 1000);
            }
        } else {
            tv_welcome_status.setText("CONNECTION INTERRUPTED");
        }
    }

    public void startAnimation(Context ctx, int view, int animation, boolean show) {
        final View Element = findViewById(view);
        if (show) {
            Element.setVisibility(View.VISIBLE);
        } else {
            Element.setVisibility(View.INVISIBLE);
        }
        Animation anim = AnimationUtils.loadAnimation(ctx, animation);
        Element.startAnimation(anim);
    }

}
