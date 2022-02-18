package com.buzz.vpn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.App;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

import static com.buzz.vpn.Data.isAppDetails;
import static com.buzz.vpn.Data.isConnectionDetails;


public class MainActivity extends AppCompatActivity implements VpnStatus.ByteCountListener, VpnStatus.StateListener {


    IOpenVPNServiceInternal mService;

    InputStream inputStream;
    BufferedReader bufferedReader;
    ConfigParser cp;
    VpnProfile vp;
    ProfileManager pm;
    Thread thread;

    // NEW
    ImageView iv_home, iv_servers, iv_data, iv_progress_bar;
    LinearLayout ll_text_bubble, ll_main_data, ll_main_today;
    TextView tv_message_top_text, tv_message_bottom_text, tv_data_text, tv_data_name;
    TextView tv_data_today, tv_data_today_text, tv_data_today_name;

    Button btn_connection;
    LottieAnimationView la_animation;

    Animation fade_in_1000, fade_out_1000;

    boolean EnableConnectButton = false;

    int progress = 0;

    String TODAY;

    CountDownTimer ConnectionTimer;
    TextView tv_main_count_down;

    // new
    boolean hasFile = false;
    String FileID = "NULL", File = "NULL", City = "NULL", Image = "NULL";
    String DarkMode = "false";

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

    ConstraintLayout constLayoutMain;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };


    @Override
    protected void onStop() {
        VpnStatus.removeStateListener(this);
        VpnStatus.removeByteCountListener(this);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences SettingsDetails = getSharedPreferences("settings_data", 0);
        DarkMode = SettingsDetails.getString("dark_mode", "false");
        constLayoutMain = findViewById(R.id.constraintLayoutMain);
        if (DarkMode.equals("true")) {
            constLayoutMain.setBackgroundColor(getResources().getColor(R.color.colorDarkBackground));
            iv_home.setImageResource(R.drawable.ic_home_white);
            iv_servers.setImageResource(R.drawable.ic_go_forward_white);
        } else {
            constLayoutMain.setBackgroundColor(getResources().getColor(R.color.colorLightBackground));
            iv_home.setImageResource(R.drawable.ic_home);
            iv_servers.setImageResource(R.drawable.ic_go_forward);
        }

        if (!isAppDetails && !isConnectionDetails) {
            try {
                Intent Welcome = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(Welcome);
            } catch (Exception e) {
                Bundle params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA1" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }
        }


        EncryptData En = new EncryptData();
        SharedPreferences ConnectionDetails = getSharedPreferences("connection_data", 0);
        FileID = ConnectionDetails.getString("file_id", "NA");
        File = En.decrypt(ConnectionDetails.getString("file", "NA"));
        File = data;
        City = ConnectionDetails.getString("city", "NA");
        Image = ConnectionDetails.getString("image", "NA");

        if (!FileID.isEmpty()) {
            hasFile = true;
        } else {
            hasFile = false;
        }

        //Log.e("connection_file", Data.FileString);


        try {
            VpnStatus.addStateListener(this);
            VpnStatus.addByteCountListener(this);
            Intent intent = new Intent(this, OpenVPNService.class);
            intent.setAction(OpenVPNService.START_SERVICE);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "MA2" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Date Today = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        TODAY = df.format(Today);

        Typeface RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface RobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface RobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        iv_home = findViewById(R.id.iv_home);
        iv_servers = findViewById(R.id.iv_servers);
        iv_data = findViewById(R.id.iv_data);
        ll_text_bubble = findViewById(R.id.ll_text_bubble);
        ll_main_data = findViewById(R.id.ll_main_data);
        ll_main_today = findViewById(R.id.ll_main_today);
        tv_message_top_text = findViewById(R.id.tv_message_top_text);
        tv_message_bottom_text = findViewById(R.id.tv_message_bottom_text);
        tv_data_text = findViewById(R.id.tv_data_text);
        tv_data_name = findViewById(R.id.tv_data_name);
        btn_connection = findViewById(R.id.btn_connection);
        la_animation = findViewById(R.id.la_animation);
        tv_data_today = findViewById(R.id.tv_data_today);
        tv_data_today_text = findViewById(R.id.tv_data_today_text);
        tv_data_today_name = findViewById(R.id.tv_data_today_name);
        tv_main_count_down = findViewById(R.id.tv_main_count_down);

        fade_in_1000 = AnimationUtils.loadAnimation(this, R.anim.fade_in_1000);
        fade_out_1000 = AnimationUtils.loadAnimation(this, R.anim.fade_out_1000);

        ll_text_bubble.setAnimation(fade_in_1000);

        tv_message_top_text.setTypeface(RobotoMedium);
        tv_message_bottom_text.setTypeface(RobotoMedium);

        tv_main_count_down.setTypeface(RobotoBold);
        btn_connection.setTypeface(RobotoBold);

        tv_data_text.setTypeface(RobotoRegular);
        tv_data_name.setTypeface(RobotoMedium);

        tv_data_today.setTypeface(RobotoMedium);
        tv_data_today_text.setTypeface(RobotoRegular);
        tv_data_today_name.setTypeface(RobotoMedium);

        LinearLayout linearLayoutMainHome = findViewById(R.id.linearLayoutMainHome);
        linearLayoutMainHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Data.isAppDetails) {
                    Intent About = new Intent(MainActivity.this, UsageActivity.class);
                    startActivity(About);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                }
            }
        });

        LinearLayout linearLayoutMainServers = findViewById(R.id.linearLayoutMainServers);
        linearLayoutMainServers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Data.isConnectionDetails) {
                    Intent Servers = new Intent(MainActivity.this, ServerActivity.class);
                    startActivity(Servers);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                }
            }
        });

        final Handler handlerToday = new Handler();
        handlerToday.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation(MainActivity.this, R.id.linearLayoutMainHome, R.anim.anim_slide_down, true);
                startAnimation(MainActivity.this, R.id.linearLayoutMainServers, R.anim.anim_slide_down, true);
            }
        }, 1000);

        btn_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (!App.isStart) {
                            if (!hasFile) {
                                Intent Servers = new Intent(MainActivity.this, ServerActivity.class);
                                startActivity(Servers);
                                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                            } else {
                                if (hasInternetConnection()) {
                                    try {
                                        start_vpn(File);
                                        final Handler handlerToday = new Handler();
                                        handlerToday.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_down_800, false);
                                            }
                                        }, 500);

                                        final Handler handlerData = new Handler();
                                        handlerData.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_data, R.anim.slide_up_800, true);
                                            }
                                        }, 1000);

                                        startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                        la_animation.cancelAnimation();
                                        la_animation.setAnimation(R.raw.conneting);
                                        la_animation.playAnimation();


                                        iv_progress_bar = findViewById(R.id.iv_progress_bar);
                                        iv_progress_bar.getLayoutParams().width = 10;
                                        progress = 10;
                                        startAnimation(MainActivity.this, R.id.iv_progress_bar, R.anim.fade_in_1000, true);

                                        tv_main_count_down.setVisibility(View.VISIBLE);
                                        App.CountDown = 30;
                                        try {
                                            ConnectionTimer = new CountDownTimer(32_000, 1000) {
                                                public void onTick(long millisUntilFinished) {
                                                    App.CountDown = App.CountDown - 1;

                                                    iv_progress_bar.getLayoutParams().width = progress;
                                                    progress = progress + (int) getResources().getDimension(R.dimen.lo_10dpGrid);
                                                    tv_main_count_down.setText(String.valueOf(App.CountDown));

                                                    if (App.connection_status == 2) {
                                                        ConnectionTimer.cancel();
                                                        SharedPreferences SharedAppDetails = getSharedPreferences("settings_data", 0);
                                                        SharedPreferences.Editor Editor = SharedAppDetails.edit();
                                                        Editor.putString("connection_time", String.valueOf(App.CountDown));
                                                        Editor.apply();
                                                        if (App.CountDown >= 20) {
                                                            SharedPreferences settings = getSharedPreferences("settings_data", 0);
                                                            String Rate = settings.getString("rate", "false");
                                                            if (Rate.equals("false")) {
                                                                Handler handler = new Handler();
                                                                handler.postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Intent Servers = new Intent(MainActivity.this, ReviewActivity.class);
                                                                        startActivity(Servers);
                                                                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                                                                    }
                                                                }, 1000);
                                                            }
                                                        }

                                                        startAnimation(MainActivity.this, R.id.tv_main_count_down, R.anim.fade_out_1000, false);
                                                        startAnimation(MainActivity.this, R.id.iv_progress_bar, R.anim.fade_out_1000, false);
                                                        startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_out_1000, false);
                                                    }

                                                    if (App.CountDown <= 20) {
                                                        EnableConnectButton = true;
                                                    }

                                                    if (App.CountDown <= 1) {
                                                        ConnectionTimer.cancel();
                                                        startAnimation(MainActivity.this, R.id.tv_main_count_down, R.anim.fade_out_500, false);
                                                        startAnimation(MainActivity.this, R.id.iv_progress_bar, R.anim.fade_out_500, false);
                                                        startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_out_500, false);

                                                        try {
                                                            stop_vpn();

                                                            final Handler handlerToday = new Handler();
                                                            handlerToday.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startAnimation(MainActivity.this, R.id.ll_main_data, R.anim.slide_down_800, false);
                                                                }
                                                            }, 500);

                                                            final Handler handlerData = new Handler();
                                                            handlerData.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                                                }
                                                            }, 1000);

                                                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                                            la_animation.cancelAnimation();
                                                            la_animation.setAnimation(R.raw.ninjainsecure);
                                                            la_animation.playAnimation();

                                                            App.ShowDailyUsage = true;
                                                        } catch (Exception e) {
                                                            Bundle params = new Bundle();
                                                            params.putString("device_id", App.device_id);
                                                            params.putString("exception", "MA3" + e.toString());
                                                            mFirebaseAnalytics.logEvent("app_param_error", params);
                                                        }
                                                        App.isStart = false;
                                                    }

                                                }

                                                public void onFinish() {

                                                }

                                            };
                                        } catch (Exception e) {
                                            Bundle params = new Bundle();
                                            params.putString("device_id", App.device_id);
                                            params.putString("exception", "MA4" + e.toString());
                                            mFirebaseAnalytics.logEvent("app_param_error", params);
                                        }
                                        ConnectionTimer.start();

                                        EnableConnectButton = false;
                                        App.isStart = true;

                                    } catch (Exception e) {
                                        Bundle params = new Bundle();
                                        params.putString("device_id", App.device_id);
                                        params.putString("exception", "MA5" + e.toString());
                                        mFirebaseAnalytics.logEvent("app_param_error", params);
                                    }

                                }
                            }
                        } else {
                            if (EnableConnectButton) {
                                try {
                                    stop_vpn();
                                    try {
                                        ConnectionTimer.cancel();
                                    } catch (Exception ignored) {
                                        //new SyncFunctions(MainActivity.this, "MA6 " +  e.toString()).set_error_log();
                                    }

                                    try {
                                        iv_progress_bar.setVisibility(View.INVISIBLE);
                                        tv_main_count_down.setVisibility(View.INVISIBLE);
                                    } catch (Exception ignored) {
                                        //new SyncFunctions(MainActivity.this, "MA7 " +  e.toString()).set_error_log();
                                    }

                                    final Handler handlerToday = new Handler();
                                    handlerToday.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startAnimation(MainActivity.this, R.id.ll_main_data, R.anim.slide_down_800, false);
                                            ll_main_data.setVisibility(View.INVISIBLE);
                                        }
                                    }, 500);


                                    final Handler handlerData = new Handler();
                                    handlerData.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                        }
                                    }, 1000);

                                    startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                    la_animation.cancelAnimation();
                                    la_animation.setAnimation(R.raw.ninjainsecure);
                                    la_animation.playAnimation();

                                    SharedPreferences settings = getSharedPreferences("settings_data", 0);
                                    String ConnectionTime = settings.getString("connection_time", "0");
                                    if (Long.valueOf(ConnectionTime) >= 20) {
                                        SharedPreferences.Editor Editor = settings.edit();
                                        Editor.putString("connection_time", "0");
                                        Editor.apply();
                                        String Rate = settings.getString("rate", "false");
                                        if (Rate.equals("false")) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent Servers = new Intent(MainActivity.this, ReviewActivity.class);
                                                    startActivity(Servers);
                                                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);

                                                }
                                            }, 500);
                                        }
                                    }


                                    App.ShowDailyUsage = true;
                                } catch (Exception e) {
                                    Bundle params = new Bundle();
                                    params.putString("device_id", App.device_id);
                                    params.putString("exception", "MA6" + e.toString());
                                    mFirebaseAnalytics.logEvent("app_param_error", params);
                                }
                                App.isStart = false;
                            }
                        }
                    }
                };
                r.run();
            }

        });


        // ui refresh
        thread = new Thread() {
            boolean ShowData = true;
            boolean ShowAnimation = true;

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // set country flag
                                if (App.abortConnection) {
                                    App.abortConnection = false;

                                    if (App.connection_status != 2) {
                                        App.CountDown = 1;
                                    }

                                    if (App.connection_status == 2) {
                                        try {
                                            stop_vpn();
                                            try {
                                                ConnectionTimer.cancel();
                                            } catch (Exception e) {
                                                Bundle params = new Bundle();
                                                params.putString("device_id", App.device_id);
                                                params.putString("exception", "MA7" + e.toString());
                                                mFirebaseAnalytics.logEvent("app_param_error", params);
                                            }

                                            iv_progress_bar.setVisibility(View.INVISIBLE);
                                            tv_main_count_down.setVisibility(View.INVISIBLE);

                                            final Handler handlerToday = new Handler();
                                            handlerToday.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startAnimation(MainActivity.this, R.id.ll_main_data, R.anim.slide_down_800, false);
                                                    ll_main_data.setVisibility(View.INVISIBLE);
                                                }
                                            }, 500);


                                            final Handler handlerData = new Handler();
                                            handlerData.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                                }
                                            }, 1000);

                                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                            la_animation.cancelAnimation();
                                            la_animation.setAnimation(R.raw.ninjainsecure);
                                            la_animation.playAnimation();

                                            App.ShowDailyUsage = true;
                                        } catch (Exception e) {
                                            Bundle params = new Bundle();
                                            params.putString("device_id", App.device_id);
                                            params.putString("exception", "MA8" + e.toString());
                                            mFirebaseAnalytics.logEvent("app_param_error", params);
                                        }
                                        App.isStart = false;
                                    }

                                }

                                switch (Image) {
                                    case "japan":
                                        iv_servers.setImageResource(R.drawable.ic_flag_japan);
                                        break;
                                    case "russia":
                                        iv_servers.setImageResource(R.drawable.ic_flag_russia);
                                        break;
                                    case "southkorea":
                                        iv_servers.setImageResource(R.drawable.ic_flag_south_korea);
                                        break;
                                    case "thailand":
                                        iv_servers.setImageResource(R.drawable.ic_flag_thailand);
                                        break;
                                    case "vietnam":
                                        iv_servers.setImageResource(R.drawable.ic_flag_vietnam);
                                        break;
                                    case "unitedstates":
                                        iv_servers.setImageResource(R.drawable.ic_flag_united_states);
                                        break;
                                    case "unitedkingdom":
                                        iv_servers.setImageResource(R.drawable.ic_flag_united_kingdom);
                                        break;
                                    case "singapore":
                                        iv_servers.setImageResource(R.drawable.ic_flag_singapore);
                                        break;
                                    case "france":
                                        iv_servers.setImageResource(R.drawable.ic_flag_france);
                                        break;
                                    case "germany":
                                        iv_servers.setImageResource(R.drawable.ic_flag_germany);
                                        break;
                                    case "canada":
                                        iv_servers.setImageResource(R.drawable.ic_flag_canada);
                                        break;
                                    case "luxemburg":
                                        iv_servers.setImageResource(R.drawable.ic_flag_luxemburg);
                                        break;
                                    case "netherlands":
                                        iv_servers.setImageResource(R.drawable.ic_flag_netherlands);
                                        break;
                                    case "spain":
                                        iv_servers.setImageResource(R.drawable.ic_flag_spain);
                                        break;
                                    case "finland":
                                        iv_servers.setImageResource(R.drawable.ic_flag_finland);
                                        break;
                                    case "poland":
                                        iv_servers.setImageResource(R.drawable.ic_flag_poland);
                                        break;
                                    case "australia":
                                        iv_servers.setImageResource(R.drawable.ic_flag_australia);
                                        break;
                                    case "italy":
                                        iv_servers.setImageResource(R.drawable.ic_flag_italy);
                                        break;
                                    default:
                                        iv_servers.setImageResource(R.drawable.ic_flag_unknown_mali);
                                        break;

                                }

                                // set connection button
                                if (hasFile) {
                                    if (App.connection_status == 0) {
                                        // disconnected
                                        btn_connection.setText("Connect");
                                        btn_connection.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_connect));

                                    } else if (App.connection_status == 1) {
                                        // connecting
                                        if (EnableConnectButton) {
                                            btn_connection.setText("Cancel");
                                            btn_connection.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_retry));
                                        } else {
                                            btn_connection.setText("Connecting");
                                            btn_connection.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_retry));
                                        }
                                    } else if (App.connection_status == 2) {
                                        // connected
                                        btn_connection.setText("Disconnect");
                                        btn_connection.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_disconnect));
                                    } else if (App.connection_status == 3) {
                                        // connected
                                        btn_connection.setText("Remove VPN Apps");
                                        btn_connection.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button_retry));

                                    }

                                }

                                // set message text
                                if (hasFile) {
                                    if (hasInternetConnection()) {
                                        if (App.connection_status == 0) {
                                            // disconnected
                                            tv_message_top_text.setText("The connection is ready");
                                            tv_message_bottom_text.setText("Tap CONNECT to start :)");

                                        } else if (App.connection_status == 1) {
                                            // connecting
                                            tv_message_top_text.setText("Connecting " + City);
                                            tv_message_bottom_text.setText(VpnStatus.getLastCleanLogMessage(MainActivity.this));

                                        } else if (App.connection_status == 2) {
                                            // connected
                                            tv_message_top_text.setText("Connected " + City);
                                            tv_message_bottom_text.setText(Data.StringCountDown);


                                        } else if (App.connection_status == 3) {
                                            // connected
                                            tv_message_top_text.setText("Dangerous VPN apps found");
                                            tv_message_bottom_text.setText("Your device at a risk, remove other VPN apps! potential dangerous VPN apps keep blocking internet connection");
                                        }
                                    } else {
                                        tv_message_top_text.setText("Connection is not available");
                                        tv_message_bottom_text.setText("Check your internet connection to continue");
                                    }

                                }

                                // show data limit
                                if (ShowData) {
                                    ShowData = false;
                                    if (App.connection_status == 0) {
                                        final Handler handlerData = new Handler();
                                        handlerData.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                            }
                                        }, 1000);
                                    } else if (App.connection_status == 1) {
                                        final Handler handlerData = new Handler();
                                        handlerData.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                            }
                                        }, 1000);
                                    } else if (App.connection_status == 2) {
                                        final Handler handlerData = new Handler();
                                        handlerData.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_data, R.anim.slide_up_800, true);
                                            }
                                        }, 1000);
                                    } else if (App.connection_status == 3) {
                                        // connected
                                        final Handler handlerData = new Handler();
                                        handlerData.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startAnimation(MainActivity.this, R.id.ll_main_today, R.anim.slide_up_800, true);
                                            }
                                        }, 1000);
                                    }
                                }

                                // get daily usage
                                if (hasFile) {
                                    if (App.connection_status == 0) {
                                        // disconnected
                                        if (App.ShowDailyUsage) {
                                            App.ShowDailyUsage = false;
                                            String PREF_USAGE = "daily_usage";
                                            SharedPreferences settings = getSharedPreferences(PREF_USAGE, 0);
                                            long long_usage_today = settings.getLong(TODAY, 0);

                                            if (long_usage_today < 1000) {
                                                tv_data_today_text.setText("1KB");
                                            } else if ((long_usage_today >= 1000) && (long_usage_today <= 1000_000)) {
                                                tv_data_today_text.setText((long_usage_today / 1000) + "KB");
                                            } else {
                                                tv_data_today_text.setText((long_usage_today / 1000_000) + "MB");
                                            }
                                        }
                                    }
                                }

                                // show animation
                                if (hasFile) {
                                    if (ShowAnimation) {
                                        ShowAnimation = false;
                                        if (App.connection_status == 0) {
                                            // disconnected
                                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                            la_animation.cancelAnimation();
                                            la_animation.setAnimation(R.raw.ninjainsecure);
                                            la_animation.playAnimation();

                                        } else if (App.connection_status == 1) {
                                            // connecting
                                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                            la_animation.cancelAnimation();
                                            la_animation.setAnimation(R.raw.conneting);
                                            la_animation.playAnimation();

                                        } else if (App.connection_status == 3) {
                                            // connected
                                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                                            la_animation.cancelAnimation();
                                            la_animation.setAnimation(R.raw.ninjainsecure);
                                            la_animation.playAnimation();
                                        }
                                    }
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Bundle params = new Bundle();
                    params.putString("device_id", App.device_id);
                    params.putString("exception", "MA9" + e.toString());
                    mFirebaseAnalytics.logEvent("app_param_error", params);
                }
            }
        };
        thread.start();

    }

    private boolean hasInternetConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        } catch (Exception e) {
            Bundle params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "MA10" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    private void start_vpn(String VPNFile) {
        SharedPreferences sp_settings;
        sp_settings = getSharedPreferences("daily_usage", 0);
        long connection_today = sp_settings.getLong(TODAY + "_connections", 0);
        long connection_total = sp_settings.getLong("total_connections", 0);
        SharedPreferences.Editor editor = sp_settings.edit();
        editor.putLong(TODAY + "_connections", connection_today + 1);
        editor.putLong("total_connections", connection_total + 1);
        editor.apply();

        Bundle params = new Bundle();
        params.putString("device_id", App.device_id);
        params.putString("city", City);
        mFirebaseAnalytics.logEvent("app_param_country", params);

        App.connection_status = 1;
        try {
            inputStream = null;
            bufferedReader = null;
            try {
                assert VPNFile != null;
                inputStream = new ByteArrayInputStream(VPNFile.getBytes(Charset.forName("UTF-8")));
            } catch (Exception e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA11" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }

            try { // M8
                assert inputStream != null;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream/*, Charset.forName("UTF-8")*/));
            } catch (Exception e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA12" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }

            cp = new ConfigParser();
            try {
                cp.parseConfig(bufferedReader);
            } catch (Exception e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA13" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }
            vp = cp.convertProfile();
            vp.mAllowedAppsVpnAreDisallowed = true;
            vp.mKeyPassword = "huaitaosha";

            EncryptData En = new EncryptData();
            SharedPreferences AppValues = getSharedPreferences("app_values", 0);
            String AppDetailsValues = En.decrypt(AppValues.getString("app_details", "NA"));

            try {
                JSONObject json_response = new JSONObject(AppDetailsValues);
                JSONArray jsonArray = json_response.getJSONArray("blocked");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json_object = jsonArray.getJSONObject(i);
                    vp.mAllowedAppsVpn.add(json_object.getString("app"));
                    Log.e("packages", json_object.getString("app"));
                }
            } catch (JSONException e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA14" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }


            try {
                vp.mName = Build.MODEL;
            } catch (Exception e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA15" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }

            vp.mUsername = Data.FileUsername;
            vp.mPassword = Data.FilePassword;

            try {
                pm = ProfileManager.getInstance(MainActivity.this);
                pm.addProfile(vp);
                pm.saveProfileList(MainActivity.this);
                pm.saveProfile(MainActivity.this, vp);
                vp = pm.getProfileByName(Build.MODEL);
                Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
                intent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUID().toString());
                intent.setAction(Intent.ACTION_MAIN);
                startActivity(intent);
                App.isStart = false;
            } catch (Exception e) {
                params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA16" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }
        } catch (Exception e) {
            params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "MA17" + e.toString());
            mFirebaseAnalytics.logEvent("app_param_error", params);
        }
    }

    public void stop_vpn() {
        App.connection_status = 0;
        OpenVPNService.abortConnectionVPN = true;
        ProfileManager.setConntectedVpnProfileDisconnected(this);

        if (mService != null) {

            try {
                mService.stopVPN(false);
            } catch (RemoteException e) {
                Bundle params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA18" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }

            try {
                pm = ProfileManager.getInstance(this);
                vp = pm.getProfileByName(Build.MODEL);
                pm.removeProfile(this, vp);
            } catch (Exception e) {
                Bundle params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA17" + e.toString());
                mFirebaseAnalytics.logEvent("app_param_error", params);
            }


        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    // does not need
    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void updateState(final String state, String logmessage, int localizedResId, ConnectionStatus level) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state.equals("CONNECTED")) {
                    App.isStart = true;
                    App.connection_status = 2;

                    Handler handlerData = new Handler();
                    handlerData.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startAnimation(MainActivity.this, R.id.la_animation, R.anim.fade_in_1000, true);
                            la_animation.cancelAnimation();
                            la_animation.setAnimation(R.raw.ninjasecure);
                            la_animation.playAnimation();
                        }
                    }, 1000);

                    EnableConnectButton = true;
                }
            }
        });
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }


    @Override
    public void updateByteCount(long ins, long outs, long diffIns, long diffOuts) {
        final long Total = ins + outs;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // size
                if (Total < 1000) {
                    tv_data_text.setText("1KB");
                    tv_data_name.setText("USED");
                } else if ((Total >= 1000) && (Total <= 1000_000)) {
                    tv_data_text.setText((Total / 1000) + "KB");
                    tv_data_name.setText("USED");
                } else {
                    tv_data_text.setText((Total / 1000_000) + "MB");
                    tv_data_name.setText("USED");
                }
            }
        });
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


