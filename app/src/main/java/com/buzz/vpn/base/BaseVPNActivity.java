package com.buzz.vpn.base;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


import com.buzz.vpn.R;
import com.buzz.vpn.databinding.ActivityAccelerateBinding;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import androidx.viewbinding.ViewBinding;
import de.blinkt.openvpn.BindUtils;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.App;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.Preferences;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

import static de.blinkt.openvpn.core.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT;

public abstract class BaseVPNActivity<T extends ViewBinding> extends BaseActivity<ActivityAccelerateBinding> implements VpnStatus.ByteCountListener, VpnStatus.StateListener{

    private String TAG = BaseVPNActivity.class.getSimpleName();

    public static final String EXTRA_KEY = "de.blinkt.openvpn.shortcutProfileUUID";
    public static final String EXTRA_NAME = "de.blinkt.openvpn.shortcutProfileName";
    public static final String EXTRA_HIDELOG = "de.blinkt.openvpn.showNoLogWindow";
    public static final String CLEARLOG = "clearlogconnect";


    private static final int START_VPN_PROFILE = 70;

    IOpenVPNServiceInternal mService;
    InputStream inputStream;
    BufferedReader bufferedReader;
    ConfigParser cp;
    VpnProfile vp;
    ProfileManager pm;
    Thread thread;
    private boolean mCmfixed = false;
    public boolean haveConnectedWifi = false;
    public boolean haveConnectedMobile = false;

    private List<VPNStatusListener> mVPNStatusListener = new ArrayList<>();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
//            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
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
//            mFirebaseAnalytics.logEvent("app_param_error", params);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        VpnStatus.removeByteCountListener(this);
        unbindService(mConnection);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public boolean hasInternetConnection() {
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
//            mFirebaseAnalytics.logEvent("app_param_error", params);
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean loadVpnProfile(String config) {
        inputStream = null;
        bufferedReader = null;
        Bundle params = null;
        try {
            assert config != null;
            inputStream = new ByteArrayInputStream(config.getBytes(Charset.forName("UTF-8")));
            assert inputStream != null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream/*, Charset.forName("UTF-8")*/));
            cp = new ConfigParser();
            cp.parseConfig(bufferedReader);
            vp = cp.convertProfile();
            vp.mAllowedAppsVpnAreDisallowed = true;
            vp.mName = Build.MODEL;
            pm = ProfileManager.getInstance(BaseVPNActivity.this);
            pm.addProfile(vp);
            pm.saveProfileList(BaseVPNActivity.this);
            pm.saveProfile(BaseVPNActivity.this, vp);
            vp = pm.getProfileByName(Build.MODEL);
            return true;
        } catch (Exception e) {
            params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "MA11" + e.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
            Log.d(TAG, "loadVpnProfile error " + e.getMessage());
            return false;
        }

//            EncryptData En = new EncryptData();
//            SharedPreferences AppValues = getSharedPreferences("app_values", 0);
//            String AppDetailsValues = En.decrypt(AppValues.getString("app_details", "NA"));
//
//            try {
//                JSONObject json_response = new JSONObject(AppDetailsValues);
//                JSONArray jsonArray = json_response.getJSONArray("blocked");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject json_object = jsonArray.getJSONObject(i);
//                    vp.mAllowedAppsVpn.add(json_object.getString("app"));
//                    Log.e("packages", json_object.getString("app"));
//                }
//            } catch (JSONException e) {
//                params = new Bundle();
//                params.putString("device_id", App.device_id);
//                params.putString("exception", "MA14" + e.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
//            }

    }

    public void startVpn() {

        Bundle params = new Bundle();
        params.putString("device_id", App.device_id);
        params.putString("city", "");
//        mFirebaseAnalytics.logEvent("app_param_country", params);

        App.connection_status = 1;
        try {

            assert vp != null;
//            Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
//            Intent intent = new Intent("de.blinkt.openvpn.ACTION_START");
//            intent.addCategory("de.blinkt.openvpn.LaunchVPN_ACTIVITY");
//            intent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUID().toString());
//            intent.setAction(Intent.ACTION_MAIN);
//            startActivity(intent);
            startVpnFromIntent();
            App.isStart = true;
        } catch (Exception e) {
            params = new Bundle();
            params.putString("device_id", App.device_id);
            params.putString("exception", "MA17" + e.toString());
            Log.d(TAG, "startVpn error " + e.getMessage());
//            mFirebaseAnalytics.logEvent("app_param_error", params);
        }
    }

    public void setAccountAndPassword(String account, String password) {
        if (getVpnProfile() == null)
            throw new IllegalStateException("You need loadVpnProfile!");

        getVpnProfile().mUsername = account;
        getVpnProfile().mPassword = password;
    }

    public void stop_vpn() {
        App.connection_status = 0;
//        OpenVPNService.abortConnectionVPN = true;
        ProfileManager.setConntectedVpnProfileDisconnected(this);

        if (mService != null) {

            try {
                mService.stopVPN(false);
            } catch (RemoteException e) {
                Bundle params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA18" + e.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
            }

            try {
                pm = ProfileManager.getInstance(this);
                vp = pm.getProfileByName(Build.MODEL);
                pm.removeProfile(this, vp);
            } catch (Exception e) {
                Bundle params = new Bundle();
                params.putString("device_id", App.device_id);
                params.putString("exception", "MA17" + e.toString());
//                mFirebaseAnalytics.logEvent("app_param_error", params);
            }


        }
    }

    protected void startVpnFromIntent() {
        // Resolve the intent

        final Intent intent = getIntent();
        final String action = intent.getAction();

        // If the intent is a request to create a shortcut, we'll do that and exit


        if (!Intent.ACTION_MAIN.equals(action)) {
            // Check if we need to clear the log
            if (Preferences.getDefaultSharedPreferences(this).getBoolean(CLEARLOG, true))
                VpnStatus.clearLog();

            // we got called to be the starting point, most likely a shortcut
            String shortcutUUID = intent.getStringExtra(EXTRA_KEY);
            String shortcutName = intent.getStringExtra(EXTRA_NAME);

            VpnProfile profileToConnect = ProfileManager.get(this, vp.getUUID().toString());
            if (shortcutName != null && profileToConnect == null) {
                profileToConnect = ProfileManager.getInstance(this).getProfileByName(shortcutName);
                if (profileToConnect == null) {
//                    finish();
                    return;
                }
            }


            if (profileToConnect == null) {
                VpnStatus.logError(R.string.shortcut_profile_notfound);
                // show Log window to display error
//                finish();
            } else {
                vp = profileToConnect;
                launchVPN();
            }
        }
    }

    void launchVPN() {
        int vpnok = vp.checkProfile(this);
        if (vpnok != R.string.no_error_found) {
            return;
        }

        Intent intent = VpnService.prepare(this);
        // Check if we want to fix /dev/tun
        SharedPreferences prefs = Preferences.getDefaultSharedPreferences(this);
        boolean usecm9fix = prefs.getBoolean("useCM9Fix", false);
        boolean loadTunModule = prefs.getBoolean("loadTunModule", false);

        if (loadTunModule)
            execeuteSUcmd("insmod /system/lib/modules/tun.ko");

        if (usecm9fix && !mCmfixed) {
            execeuteSUcmd("chown system /dev/tun");
        }

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                VpnStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }

    }

    private void execeuteSUcmd(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
            Process p = pb.start();
            int ret = p.waitFor();
            if (ret == 0)
                mCmfixed = true;
        } catch (InterruptedException | IOException e) {
            VpnStatus.logException("SU command", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                if (vp == null) return;
                ProfileManager.updateLRU(this, vp);
                VPNLaunchHelper.startOpenVpn(vp, getBaseContext());
//                finish();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                VpnStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                        ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    VpnStatus.logError(R.string.nought_alwayson_warning);

//                finish();
            }
        }
    }

    // does not need
    @Override
    public void finish() {
        super.finish();
    }

    public void addVPNStatusListener(VPNStatusListener listener) {
        if (!mVPNStatusListener.contains(listener)) {
            mVPNStatusListener.add(listener);
        }
    }

    public void removeVPNStatusListener(VPNStatusListener listener) {
        if (!mVPNStatusListener.contains(listener)) {
            mVPNStatusListener.remove(listener);
        }
    }

    public VpnProfile getVpnProfile() {
        return vp;
    }

    public boolean isRunning() {
        return VpnStatus.isVPNActive();
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        BindUtils.bindStatus(state, logmessage, localizedResId, level);
        // 分发到事件监听
        for (VPNStatusListener vpnStatusListener : mVPNStatusListener) {
            switch (level) {
                case LEVEL_START:
                    // 开始连接
                    vpnStatusListener.onConnectStart();
                    break;
                case LEVEL_CONNECTED:
                    // 已连接
                    vpnStatusListener.onConnected();
                    break;
                case LEVEL_VPNPAUSED:
                    // 暂停
                    vpnStatusListener.onPaused();
                    break;
                case LEVEL_NONETWORK:
                    // 无网络
                    vpnStatusListener.onNoNetwork();
                    break;
                case LEVEL_CONNECTING_SERVER_REPLIED:
                    // 服务器答应
//                    vpnStatusListener.onServerReplied();
                    break;
                case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
                    // 服务器不答应
//                    vpnStatusListener.onServerNoReplied();
                    break;
                case LEVEL_NOTCONNECTED:
                    // 连接关闭
                    vpnStatusListener.onConnectClose();
                    break;
                case LEVEL_AUTH_FAILED:
                    // 认证失败
                    vpnStatusListener.onAuthFailed();
                    break;
                case LEVEL_WAITING_FOR_USER_INPUT:
                    // 等待用户输入
                    Log.d(TAG, "updateState: " + LEVEL_WAITING_FOR_USER_INPUT);
                    break;
                case UNKNOWN_LEVEL:
                    // 未知错误
                    vpnStatusListener.onUnknown();
                    break;
            }
        }
    }

    @Override
    public void setConnectedVPN(String uuid) {

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
        Log.d(TAG, "usage:"+usage);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public abstract Intent getJumpIntent();

    public interface VPNStatusListener {
        void onConnectStart();

        void onConnected();

        void onPaused();

//        void onServerReplied();
//
//        void onServerNoReplied();

        void onNoNetwork();

        void onConnectClose();

        void onAuthFailed();

        void onUnknown();
    }
}
