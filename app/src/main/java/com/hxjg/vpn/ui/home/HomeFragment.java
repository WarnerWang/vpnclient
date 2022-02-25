package com.hxjg.vpn.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hxjg.vpn.Data;
import com.hxjg.vpn.R;
import com.hxjg.vpn.api.bean.commonBean.app.SeriDrawable;
import com.hxjg.vpn.databinding.FragmentHomeBinding;
import com.hxjg.vpn.ui.accelerate.AccelerateActivity;
import com.hxjg.vpn.api.bean.commonBean.app.AppInfo;
import com.hxjg.vpn.api.bean.req.app.AccelerateAppListReq;
import com.hxjg.vpn.api.bean.resp.app.AccelerateAppListResp;
import com.hxjg.vpn.api.network.Api;
import com.hxjg.vpn.base.BaseFragment;
import com.hxjg.vpn.ui.accelerate.WireguardAccelerateActivity;
import com.hxjg.vpn.utils.AppUtil;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.ScreenUtils;
import com.hxjg.vpn.utils.TokenCache;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import de.blinkt.openvpn.core.VpnStatus;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    RecyclerView recyclerView;
    BGARefreshLayout refreshLayout;
    TextView tvToolbarTitle;
    ImageButton ibToolbarBack;

    private BaseQuickAdapter<AppInfo, BaseViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        tvToolbarTitle = (TextView)view.findViewById(R.id.tv_toolbar_title);
        ibToolbarBack = (ImageButton)view.findViewById(R.id.ib_toolbar_back);
        recyclerView = viewBinding.recyclerView;
        refreshLayout = viewBinding.refreshLayout;
        tvToolbarTitle.setText("加速");
        ibToolbarBack.setVisibility(View.GONE);
        initRefreshLayout();
        initRecycleView();
        int statusBarHeight = ScreenUtils.getStatusBarHeight(getActivity());
        int navigationBarHeight = ScreenUtils.getNavigationBarHeight();
        Logger.i("statusBarHeight="+statusBarHeight+", navigationBarHeight="+navigationBarHeight);
//        loadLocalApp();
        refreshLayout.beginRefreshing();
        AppUtil.getNetIp();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshLayout.beginRefreshing();
        adapter.notifyDataSetChanged();
    }

    private void initRefreshLayout() {
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), false);
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(refreshViewHolder);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                Logger.i("refresh ..");
                loadLocalApp();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                Logger.i("load more ..");
                return false;
            }
        });

    }

    private void initRecycleView() {
        adapter = new BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_layout_home_app) {
            @Override
            protected void convert(BaseViewHolder helper, AppInfo item) {
                boolean isAccelerate = VpnStatus.isVPNActive() && item.getBundleId().equals(TokenCache.getIns().getUseVpnPackageName());
                helper.setText(R.id.app_name, item.getApplicationName())
                .setText(R.id.accelerate_btn, isAccelerate ? "加速中" : "加速");
                helper.addOnClickListener(R.id.accelerate_btn);
                ImageView appIcon = helper.getView(R.id.app_icon);
                appIcon.setImageDrawable(item.getAppDrawable().getDrawable());
//                ImageLoad.loadImage(appIcon.getContext(), item.getApplicationIcon(),appIcon);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.accelerate_btn) {
                    AppInfo item = (AppInfo) adapter.getItem(position);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_NAME, item.getApplicationName());
//                    bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_PACKAGE, item.getBundleId());
//                    bundle.putSerializable(AccelerateActivity.KEY_ACCELERATE_APP_INFO,item);
//                    AccelerateActivity.startMe(getActivity(), AccelerateActivity.class, bundle);
//                    showActionSheet(item);
                    pushToNextActivity(item, 0);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter.bindToRecyclerView(recyclerView);
    }

    public void showActionSheet(AppInfo item){
        List<String> list = new ArrayList<>();
        list.add("使用OpenVpn");
        list.add("使用wireGuard");
        if (Data.vpnType != -1) {
            pushToNextActivity(item, Data.vpnType);
            return;
        }
        new MaterialDialog.Builder(getActivity())
                .title("标题")
                .items(list)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        pushToNextActivity(item, which);
                        return true;
                    }
                })
                .show();
    }

    public void pushToNextActivity(AppInfo item, int type){
        Logger.i("use vpn type = "+type);

        Bundle bundle = new Bundle();
        bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_NAME, item.getApplicationName());
        bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_PACKAGE, item.getBundleId());
        bundle.putSerializable(AccelerateActivity.KEY_ACCELERATE_APP_INFO,item);
        if (type == 0) {
            AccelerateActivity.startMe(getActivity(), AccelerateActivity.class, bundle);

        }else if (type == 1){
            WireguardAccelerateActivity.startMe(getActivity(), WireguardAccelerateActivity.class, bundle);
        }
    }

    private void loadAppList(){
        AccelerateAppListReq req = new AccelerateAppListReq();
        Subscription subscription = Api.ins()
                .getAppAPI()
                .accelerateAppList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccelerateAppListResp>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("refresh onCompleted");
                        refreshLayout.endRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AccelerateAppListResp resp) {
                        refreshLayout.endRefreshing();
                        loadData(resp.getList());
                    }
                });
        addSubscription(subscription);
    }

    private void loadData(List<AppInfo> appList){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.endRefreshing();
                adapter.setNewData(appList);
            }
        });


    }

    private void loadLocalApp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, Object>> appPackageItems = AppUtil.getAppPackageItems(getContext());
                List<AppInfo> appList = new ArrayList<>();
                for (int i = 0; i < appPackageItems.size(); i++) {
                    HashMap<String, Object> hashMap = appPackageItems.get(i);
                    AppInfo appInfo = new AppInfo();
                    Drawable appimage = (Drawable) hashMap.get("appimage");
//            AppDrawable appDrawable = new AppDrawable(appimage);
                    SeriDrawable seriDrawable = new SeriDrawable(appimage);
                    appInfo.setAppDrawable(seriDrawable);
                    appInfo.setApplicationName(hashMap.get("appName").toString());
                    appInfo.setBundleId(hashMap.get("packageName").toString());
                    appList.add(appInfo);
                }
                loadData(appList);
            }
        }).start();


    }
}
