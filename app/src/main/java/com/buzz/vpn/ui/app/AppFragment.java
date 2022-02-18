package com.buzz.vpn.ui.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.buzz.vpn.R;
import com.buzz.vpn.databinding.FragmentAppBinding;
import com.buzz.vpn.ui.accelerate.AccelerateActivity;
import com.buzz.vpn.api.bean.commonBean.app.AppInfo;
import com.buzz.vpn.api.bean.req.app.QueryApplicationInfoListReq;
import com.buzz.vpn.api.bean.resp.app.QueryApplicationInfoListResp;
import com.buzz.vpn.api.image.ImageLoad;
import com.buzz.vpn.api.network.Api;
import com.buzz.vpn.base.BaseFragment;
import com.buzz.vpn.ui.common.RecyclerItemDecoration;
import com.buzz.vpn.utils.Logger;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppFragment extends BaseFragment<FragmentAppBinding> {

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
        tvToolbarTitle.setText("应用库");
        ibToolbarBack.setVisibility(View.GONE);
        initRefreshLayout();
        initRecycleView();
        refreshLayout.beginRefreshing();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initRefreshLayout() {
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), false);
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(refreshViewHolder);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                Logger.i("refresh ..");
                loadAppList();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                Logger.i("load more ..");
                return false;
            }
        });

    }

    private void initRecycleView() {
        adapter = new BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_layout_labrary_app) {
            @Override
            protected void convert(BaseViewHolder helper, AppInfo item) {
                helper.setText(R.id.app_name, item.getApplicationName());
                helper.addOnClickListener(R.id.accelerate_btn);
                ImageView appIcon = helper.getView(R.id.app_icon);
                ImageLoad.loadImage(appIcon.getContext(), item.getApplicationIcon(),appIcon);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(24, 3));
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.accelerate_btn) {
                    AppInfo item = (AppInfo) adapter.getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_NAME, item.getApplicationName());
                    bundle.putString(AccelerateActivity.KEY_ACCELERATE_APP_PACKAGE, item.getBundleId());
                    bundle.putSerializable(AccelerateActivity.KEY_ACCELERATE_APP_INFO,item);
                    AccelerateActivity.startMe(getActivity(), AccelerateActivity.class, bundle);
                }
            }
        });
    }

    private void loadAppList(){
        QueryApplicationInfoListReq req = new QueryApplicationInfoListReq();
        Subscription subscription = Api.ins()
                .getAppAPI()
                .queryApplicationInfoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QueryApplicationInfoListResp>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("refresh onCompleted");
                        refreshLayout.endRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QueryApplicationInfoListResp resp) {
                        refreshLayout.endRefreshing();
                        loadData(resp.getList());
                    }
                });
        addSubscription(subscription);
    }

    private void loadData(List<AppInfo> appList){
        adapter.setNewData(appList);
    }
}
