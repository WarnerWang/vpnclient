package com.hxjg.vpn.ui.tabbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxjg.vpn.R;
import com.hxjg.vpn.base.BaseActivity;
import com.hxjg.vpn.common.WebViewFragment;
import com.hxjg.vpn.databinding.ActivityTabbarBinding;
import com.hxjg.vpn.ui.app.AppFragment;
import com.hxjg.vpn.ui.home.HomeFragment;
import com.hxjg.vpn.ui.mine.MineFragment;
import com.hxjg.vpn.utils.Logger;
import com.hxjg.vpn.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.HashMap;
import java.util.List;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 程序入口
 * 主程序
 */
public class TabBarActivity extends BaseActivity<ActivityTabbarBinding> {
    private static final String KEY_PAGE_OFFSET = "PAGE_OFFSET";

    ViewPager viewPager;
    TabLayout bottomTabLayout;
    private final int[] iconArray = {
            R.mipmap.accelerate_n,
            R.mipmap.app_n,
            R.mipmap.mine_n};
    private final int[] iconPressedArray = {
            R.mipmap.accelerate_s,
            R.mipmap.app_s,
            R.mipmap.mine_s};
    private final String[] tabTextArray = {
            "加速",
            "应用库",
            "我的"};

//    private List<LocalMedia> selectList = Lists.newArrayList();

    public static TabBarActivity sInstance = null;

    private long mPressedTime = 0;

    public static void startMe(Context context, int pageOffset) {
        Intent intent = new Intent(context, TabBarActivity.class);
        intent.putExtra(KEY_PAGE_OFFSET, pageOffset);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (context instanceof Activity) {

        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);

    }

    public static Intent getStartIntent(Context context, int pageOffset) {
        Intent intent = new Intent(context, TabBarActivity.class);
        intent.putExtra(KEY_PAGE_OFFSET, pageOffset);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (context instanceof Activity) {

        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ((Activity) context).overridePendingTransition(0, 0);
        return intent;
    }

    public static void startMe(Context context) {
        Intent intent = new Intent(context, TabBarActivity.class);
        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(0, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        viewPager = viewBinding.homeViewPager;
        bottomTabLayout = viewBinding.bottomTabLayout;
//        transparentStatusBar();
        setStatusBarColor(R.color.backgroundColor);
        initViewPager();
//        FileUriUtils.startFor("/data/user",this, REQUEST_CODE_FOR_DIR);
//        loadPermissions();
    }

    private void loadPermissions(){
        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
                //.permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                .permission(new String[]{Permission.MANAGE_EXTERNAL_STORAGE})
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Logger.i("获取存储权限成功");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Logger.i("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(TabBarActivity.this, permissions);
                        } else {
                            Logger.i("获取存储权限失败");
                        }
                    }
                });
    }

    public void initViewPager(){
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 修改状态栏字体颜色
                if (tab.getPosition() == 3) {
                    setStatusBarDarkFont(true);
                } else {
                    setStatusBarDarkFont(false);
                }
//                setStatusBarDarkFont(false);

                viewPager.setCurrentItem(tab.getPosition());

                //改变Tab 状态
                for (int i = 0; i < bottomTabLayout.getTabCount(); i++) {
                    TabLayout.Tab tabAt = bottomTabLayout.getTabAt(i);
                    if (tabAt == null) {
                        Logger.e("tab is null");
                        return;
                    }

                    View customView = tabAt.getCustomView();
                    if (customView == null) {
                        return;
                    }

                    ImageView imageView = customView.findViewById(R.id.iv_icon);
                    TextView textView = customView.findViewById(R.id.tv_text);

                    if (i == tab.getPosition()) {
                        imageView.setImageResource(iconPressedArray[i]);
                        textView.setTextColor(Color.parseColor("#F5A623"));
                    } else {
                        imageView.setImageResource(iconArray[i]);
                        textView.setTextColor(Color.parseColor("#9EA4BA"));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (int i = 0; i < iconArray.length; i++) {
            String title = tabTextArray[i];
            View view = LayoutInflater.from(this).inflate(R.layout.item_layout_main_tab, null);
            ImageView ivIcon = view.findViewById(R.id.iv_icon);
            ivIcon.setImageResource(iconArray[i]);
            TextView tvText = view.findViewById(R.id.tv_text);
            tvText.setText(tabTextArray[i]);
            bottomTabLayout.addTab(
                    bottomTabLayout.newTab().setCustomView(view)
            );
        }
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), bottomTabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bottomTabLayout));
        viewPager.setOffscreenPageLimit(bottomTabLayout.getTabCount() - 1);

        viewPager.setCurrentItem(1);
        int pageOffset = getIntent().getIntExtra(KEY_PAGE_OFFSET, 0);
        if (pageOffset >= 0 && pageOffset < iconArray.length) {
            viewPager.setCurrentItem(pageOffset);
        }

    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int pageOffset = intent.getIntExtra(KEY_PAGE_OFFSET, 0);
        if (pageOffset >= 0 && pageOffset < iconArray.length) {
            viewPager.setCurrentItem(pageOffset);
        }
    }

//    public void chooseImage() {
//        PictureSelector.create(this)
//                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                .theme(R.style.picture_QQ_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
//                .maxSelectNum(1)// 最大图片选择数量
//                .minSelectNum(1)// 最小选择数量
//                .imageSpanCount(4)// 每行显示个数
//                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
//                .previewImage(true)// 是否可预览图片
//                .previewVideo(true)// 是否可预览视频
//                .enablePreviewAudio(false) // 是否可播放音频
//                .isCamera(true)// 是否显示拍照按钮
//                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
//                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
//                .enableCrop(true)// 是否裁剪
//                .compress(true)// 是否压缩
//                .synOrAsy(true)//同步true或异步false 压缩 默认同步
//                //.compressSavePath(getPath())//压缩图片保存地址
//                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
//                .isGif(false)// 是否显示gif图片
//                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
//                .circleDimmedLayer(false)// 是否圆形裁剪
//                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                .openClickSound(false)// 是否开启点击声音
//                .selectionMedia(selectList)// 是否传入已选图片
//                .isDragFrame(true)// 是否可拖动裁剪框(固定)
////                        .videoMaxSecond(15)
////                        .videoMinSecond(10)
//                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
//                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
//                .minimumCompressSize(100)// 小于100kb的图片不压缩
//                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
//                //.rotateEnabled(true) // 裁剪是否可旋转图片
//                //.scaleEnabled(true)// 裁剪是否可放大缩小图片
//                //.videoQuality()// 视频录制质量 0 or 1
//                //.videoSecond()//显示多少秒以内的视频or音频也可适用
//                //.recordVideoSecond()//录制视频秒数 默认60s
//                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
//    }

    public void setCurrentPage(int pageOffset) {
        if (pageOffset >= 0 && pageOffset < iconArray.length) {
            viewPager.setCurrentItem(pageOffset);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("onActivityResult requestCode:%d resultCode:%d", requestCode, resultCode);
        if (requestCode == REQUEST_CODE_FOR_DIR && resultCode == Activity.RESULT_OK) {
            Uri uriTree = null;
            if (data != null) {
                uriTree = data.getData();
            }
            if (uriTree != null) {
                // create DocumentFile which represents the selected directory
                DocumentFile root = DocumentFile.fromTreeUri(this, uriTree);
                // list all sub dirs of root
                DocumentFile[] files = root.listFiles();
                // do anything you want with APIs provided by DocumentFile
                // ...
            }
        }
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case PictureConfig.CHOOSE_REQUEST:
//                    // 图片、视频、音频选择结果回调
//                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
//                    // 例如 LocalMedia 里面返回三种path
//                    // 1.media.getPath(); 为原图path
//                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
//                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
//                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    Logger.i("selectList:" + JsonUtil.toJSONString(selectList));
//                    if (!CollectionUtils.isEmpty(selectList)) {
//                        String imagePath;
//                        LocalMedia localMedia = selectList.get(0);
//                        if (localMedia.isCut()) {
//                            imagePath = localMedia.getCutPath();
//                        } else if (localMedia.isCompressed()) {
//                            imagePath = localMedia.getCompressPath();
//                        } else {
//                            imagePath = localMedia.getPath();
//                        }
//                        doUploadImage(imagePath);
//                    } else {
//                        Logger.i("select list is null");
//                    }
//                    break;
//            }
//        }
//        if (requestCode != PictureConfig.CHOOSE_REQUEST) {
//            try {
//                PageAdapter adapter = (PageAdapter) viewPager.getAdapter();
//                for (int i = 0; i < adapter.mFragmentHashMap.size(); i++) {
//                    BaseFragment fragment = (BaseFragment) adapter.mFragmentHashMap.get(i);
//                    fragment.onResultRefresh(requestCode, resultCode, data);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
    }

//    private void doUploadImage(String imagePath) {
//        Subscription subscription = Api.ins().getUploadService()
//                .uploadHeadImg(imagePath)
//                .subscribe(new Subscriber<UploadResp>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Logger.printException(e);
//                        if (e instanceof ServerError) {
//                            ToastUtil.ins().show(((ServerError) e).getMsg());
//                        } else {
//                            ToastUtil.ins().show("图片上传失败");
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UploadResp body) {
//                        EventBus.getDefault().post(new IEvent.UpdateUserInfo());
//                    }
//                });
//        addSubscription(subscription);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {

        PageAdapter adapter = (PageAdapter) viewPager.getAdapter();
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        if (fragment instanceof WebViewFragment) {
            if (((WebViewFragment) fragment).mAgentWeb.handleKeyEvent(KeyEvent.KEYCODE_BACK, null)) {
                return;
            }
        }

        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            ToastUtil.ins().show("再按一次退出程序");
            mPressedTime = mNowTime;
        } else {//退出程序
            this.finish();
            System.exit(0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    public static class PageAdapter extends FragmentPagerAdapter {

        private int num;
        public HashMap<Integer, Fragment> mFragmentHashMap = new HashMap<>();

        PageAdapter(FragmentManager fm, int num) {
            super(fm);
            this.num = num;
        }

        @Override
        public Fragment getItem(int position) {

            return createFragment(position);
        }

        @Override
        public int getCount() {
            return num;
        }

        private Fragment createFragment(int pos) {
            Fragment fragment = mFragmentHashMap.get(pos);

            if (fragment == null) {
                switch (pos) {
                    case 0:
                        fragment = new HomeFragment();
                        break;
                    case 1:
                        fragment = new AppFragment();
                        break;
                    case 2:
                        fragment = new MineFragment();
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
                mFragmentHashMap.put(pos, fragment);
            }
            return fragment;
        }
    }

}
