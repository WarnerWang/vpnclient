package com.hxjg.vpn.ui.accelerate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hxjg.vpn.Data;
import com.hxjg.vpn.R;
import com.hxjg.vpn.api.bean.base.BaseResp;
import com.hxjg.vpn.api.bean.commonBean.app.AppInfo;
import com.hxjg.vpn.api.bean.req.app.AccelerateApplyReq;
import com.hxjg.vpn.api.bean.req.app.AccelerateDeleteReq;
import com.hxjg.vpn.api.bean.resp.app.AccelerateApplyResp;
import com.hxjg.vpn.api.image.ImageLoad;
import com.hxjg.vpn.api.network.Api;
import com.hxjg.vpn.base.BaseVPNActivity;
import com.hxjg.vpn.common.LongRunningService;
import com.hxjg.vpn.databinding.ActivityAccelerateBinding;
import com.hxjg.vpn.manager.IperfManager;
import com.hxjg.vpn.manager.UserManage;
import com.hxjg.vpn.ui.tabbar.TabBarActivity;
import com.hxjg.vpn.utils.AppUtil;
import com.hxjg.vpn.utils.DateUtil;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.StringUtils;
import com.hxjg.vpn.utils.ToastUtil;
import com.hxjg.vpn.utils.TokenCache;
import com.hxjg.vpn.utils.TrafficStatsManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.blinkt.openvpn.BindStatus;
import de.blinkt.openvpn.BindUtils;
import de.blinkt.openvpn.StatusInfo;
import de.blinkt.openvpn.core.OpenVPNManagement;
import de.blinkt.openvpn.core.TrafficHistory;
import de.blinkt.openvpn.core.VpnStatus;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static de.blinkt.openvpn.core.OpenVPNService.humanReadableByteCount;
import static java.lang.Math.max;


public class WireguardAccelerateActivity extends BaseVPNActivity<ActivityAccelerateBinding> {
    public final static String KEY_ACCELERATE_APP_INFO = "KEY_ACCELERATE_APP_INFO";
    public final static String KEY_ACCELERATE_APP_NAME = "KEY_ACCELERATE_APP_NAME";
    public final static String KEY_ACCELERATE_APP_PACKAGE = "KEY_ACCELERATE_APP_PACKAGE";

    private static final int TIME_PERIOD_SECDONS = 0;
    private static final int TIME_PERIOD_MINUTES = 1;
    private static final int TIME_PERIOD_HOURS = 2;

    String data = "[Interface]\n" +
            "PrivateKey = UEa/ZvVwc5d53u6N/LWYA4EUmOpRDRvg1xc8/GZRCk8=\n" +
            "Address = 10.66.66.2/32,fd42:42:42::2/128\n" +
            "DNS = 8.8.8.8\n" +
            "\n" +
            "[Peer]\n" +
            "PublicKey = ugvT8UDI5xLl6KbGQHzQ2pVsrPiUK1rHcKGCLzVcxHM=\n" +
            "PresharedKey = yBXzdPfrMLpAbL/6z/dsTgsqxhkri7qFpsWJe5cUilY=\n" +
            "Endpoint = 39.105.52.99:51820\n" +
            "AllowedIPs = 0.0.0.0/0,::/0";

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
    private Timer timer;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, WireguardAccelerateActivity.class);
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
                viewBinding.accelerateAppIcon.setImageDrawable(appInfo.getAppDrawable().getDrawable());
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
                        stopActivityVpn();
                    }else {
                        startActivityVpn();
                    }
                };

            }
        });
        viewBinding.accelerateDown.setText("0");
        viewBinding.accelerateUp.setText("0");
        mHandler = new Handler();
        mColourIn = getResources().getColor(R.color.line_in);
        mColourOut = getResources().getColor(R.color.line_out);
        List<Integer> charts = new LinkedList<>();
        charts.add(TIME_PERIOD_SECDONS);
//        charts.add(TIME_PERIOD_MINUTES);
//        charts.add(TIME_PERIOD_HOURS);

        mChartAdapter = new ChartDataAdapter(this, charts);
        viewBinding.graphListview.setAdapter(mChartAdapter);

        viewBinding.accelerateSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSpeed(true);
            }
        });
        if (VpnStatus.isVPNActive()) {
            startTimer();
        }
    }

    public void startTimer(){
        if (timer != null) {
            return;
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (timer == null) {
                    return;
                }
                switch (msg.what) {
                    case 200: {

                        viewBinding.accelerateTime.setText(DateUtil.secToTime(Data.connectSecond));
                    }
                    break;
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(200);
            }
        }, 0, 1000);
    }

    public void testSpeed(boolean down){
        showProgressDialog();
        IperfManager.sendIperf(down,new IperfManager.OnIperfRequestResultListener() {
            @Override
            public void onSuccess() {
                Logger.i("success");
            }

            @Override
            public void onError(String e) {
                Logger.i("error"+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        ToastUtil.ins().show("error="+e, true);
                    }
                });
            }

            @Override
            public void onUpdate(String s) {
                Logger.i("update="+s);
            }

            @Override
            public void onResult(String result) {
                Logger.i("testSpeed result="+result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        if (down) {
                            testSpeed(false);
                            viewBinding.localDown.setText(result);
                        }else {
                            viewBinding.localUp.setText(result);
                        }
                    }
                });
            }
        });
    }

    public void startActivityVpn(){
//        loadVpnProfile(data);
//        getVpnProfile().mName = "vpn";
//        getVpnProfile().mKeyPassword = "huaitaosha";
//        getVpnProfile().mAllowedAppsVpnAreDisallowed = false;
//        HashSet<String> allowApp = new HashSet<>();
//        allowApp.add(packageName);
//        getVpnProfile().mAllowedAppsVpn=allowApp;
//        startVpn();
        try {
//            WireguardManager.getIns().startVpnProfile(data, packageName);
            startTimer();
            TokenCache.getIns().saveUseVpnPackageName(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopActivityVpn(){
        TokenCache.getIns().saveUseVpnPackageName(null);
//        stop_vpn();
//        WireguardManager.getIns().stopVpn();
        Data.connectSecond = 0;
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
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
            viewBinding.accelerateTime.setVisibility(View.VISIBLE);
            viewBinding.accelerateBtn.setText("停止加速");
            viewBinding.accelerateBtn.setSolidColor(R.color.transparent);
            viewBinding.accelerateBtn.setBorderColor(R.color.border_color);
        }else {
            viewBinding.accelerateTime.setVisibility(View.INVISIBLE);
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
        String totalByteString = StringUtils.formatByte(Total);

        String downByteString = StringUtils.formatByte(diffIn);
        String upByteString = StringUtils.formatByte(diffOut);

        Logger.i("total usage = "+ Data.LongDataUsage);
        appUsage = totalByteString;
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
        TrafficStatsManager.getIns().updateNetSpeed();
        String localDownByteString = StringUtils.formatByte(TrafficStatsManager.getIns().getDownSpeed());
        String localUpByteString = StringUtils.formatByte(TrafficStatsManager.getIns().getUpSpeed());
        Logger.i("vpn downByteString="+downByteString+", upByteString="+upByteString);
        Logger.i("local downByteString="+localDownByteString+", upByteString="+localUpByteString);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textV.setText("当前消耗流量："+appUsage);
                mHandler.removeCallbacks(triggerRefresh);
                viewBinding.accelerateUsage.setText(appUsage);
                viewBinding.accelerateDown.setText(downByteString);
                viewBinding.accelerateUp.setText(upByteString);
//                viewBinding.localDown.setText("↓:"+localDownByteString);
//                viewBinding.localUp.setText("↑:"+localUpByteString);
                mChartAdapter.notifyDataSetChanged();
                mHandler.postDelayed(triggerRefresh, OpenVPNManagement.mBytecountInterval * 1500);
            }
        });
    }

    @Override
    public void onDestroy() {
        BindUtils.unBind(this);
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
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
        req.setApplicationInfoId((appInfo == null || StringUtils.isEmpty(appInfo.getApplicationInfoId())) ? "1" : appInfo.getApplicationInfoId());
        req.setMobile(UserManage.ins().getUserInfo().getPhone());
        req.setPrivateIp(AppUtil.getLocalIPAddress());
        req.setPublicIp(TokenCache.getIns().getNetIpAddress());
//        req.setPrivateIp("10.96.197.247");
//        req.setPublicIp("112.224.163.230");
        List<String> targetIpList = new ArrayList<>();
        targetIpList.add("39.105.52.99");
//        targetIpList.add(appInfo.getTargetIp());
        req.setTargetIp(targetIpList);
        showProgressDialog();
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
                            dismissDialog();
                            ToastUtil.ins().show(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(AccelerateApplyResp resp) {
                        startActivityVpn();
                        dismissDialog();
                    }
                });
        addSubscription(subscription);
    }

    private void accelerateDelete(boolean needReRequest){
        AccelerateDeleteReq req = new AccelerateDeleteReq();
        req.setApplicationInfoId((appInfo == null || StringUtils.isEmpty(appInfo.getApplicationInfoId())) ? "1" : appInfo.getApplicationInfoId());
        req.setApplyOrderId("");
        req.setMobile(UserManage.ins().getUserInfo().getPhone());
        if (!needReRequest) {
            showProgressDialog();
        }
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
                        dismissDialog();
                        ToastUtil.ins().show(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        dismissDialog();
                        stopActivityVpn();
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
                holder.title.setTextColor(getResources().getColor(R.color.colorWhite));
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            mTextColour = getResources().getColor(R.color.colorWhite);
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
