package com.haohai.platform.fireforestplatform;


import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.cretin.www.cretinautoupdatelibrary.interfaces.AppDownloadListener;
import com.cretin.www.cretinautoupdatelibrary.interfaces.MD5CheckListener;
import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.ActivityMainBinding;
import com.haohai.platform.fireforestplatform.event.DoUpdate;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.MessageChange;
import com.haohai.platform.fireforestplatform.event.Update;
import com.haohai.platform.fireforestplatform.ui.bean.VersionBean;
import com.haohai.platform.fireforestplatform.ui.fragment.MainFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MapFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MessageFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MineFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.VideoFragment;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MainViewModel;
import com.haohai.platform.fireforestplatform.utils.BottomBarUtils;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.kongzue.dialogx.dialogs.PopNotification;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class MainActivity extends BaseLiveActivity<ActivityMainBinding, MainViewModel> implements BottomNavigationBar.OnTabSelectedListener{
    private MainFragment mainFragment;
    private VideoFragment videoFragment;
    private MapFragment mapFragment;
    private MineFragment mineFragment;
    private MessageFragment messageFragment;
    private TextBadgeItem textBadgeItem;
    private Fragment[] fragments;
    private int index = 0;
    private int currentTabIndex = 0;
    private final boolean isMain = false;



    private LBSTraceClient mTraceClient;
    private com.baidu.trace.Trace mTrace;
    private BottomNavigationItem messageBottomNavigationItem;

    private void initBDTrace() {
        // 轨迹服务ID
        long serviceId = 235910;
        // 设备标识
        String entityName = (String) SPUtils.get(this, SPValue.id,"10000000001");//"10000000001";
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        mTrace = new com.baidu.trace.Trace(serviceId, entityName, isNeedObjectStorage);
        //同意隐私政策
        LBSTraceClient.setAgreePrivacy(this, true);
        // 初始化轨迹服务客户端
        try {
            mTraceClient = new LBSTraceClient(getApplicationContext());
            CommonData.mTraceClient = mTraceClient;
            // 定位周期(单位:秒)
            int gatherInterval = 5;
            // 打包回传周期(单位:秒)
            int packInterval = 10;
            // 设置定位和打包周期
            mTraceClient.setInterval(gatherInterval, packInterval);
            // 开启服务
            mTraceClient.startTrace(mTrace, mTraceListener);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "initBDTrace: BDTrace" + e.toString() );
        }
    }

    // 初始化轨迹服务监听器
    OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
            Log.e("TAG", "BDTrace onBindServiceCallback: " + i + s );
        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            Log.e("TAG", "BDTrace onStartTraceCallback: " + status + message );
            if(status == 0){
                // 开启采集
                mTraceClient.startGather(mTraceListener);
            }
        }
        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            Log.e("TAG", "BDTrace onStopTraceCallback: " + message );
        }
        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            Log.e("TAG", "BDTrace onStartGatherCallback: " + status +message );
        }
        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            Log.e("TAG", "BDTrace onStopGatherCallback: " +message );
        }
        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
            Log.e("TAG", "BDTrace onPushCallback: " );
        }

        @Override
        public void onInitBOSCallback(int i, String s) {
            Log.e("TAG", "BDTrace onInitBOSCallback: " );
        }

        @Override
        public void onTraceDataUploadCallBack(int i, String s, int i1, int i2) {
            Log.e("TAG", "BDTrace onTraceDataUploadCallBack: " );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        EventBus.getDefault().register(this);
        initBDTrace();

        initBottomBar();

        //延时获取通知栏权限
        requestNotificationPermission();

        //MessageDialog.show("温馨提示", "这是一条测试2.0提示","好的");
        /*PopNotification.build()
                .setIconResId(R.drawable.ic_icon)
                .setMessage("欢迎进入"+getResources().getString(R.string.app_name))
                .setMargin(20,20,20,0)
                .setRadius(40)
                .setTitle("您有一条新消息")
                .show();*/

        CommonData.walkDistance = (int) SPUtils.get(this, SPValue.walk, CommonData.walkDistance);
    }

    private boolean show = true;
    @Override
    protected void onResume() {
        super.onResume();
        CommonData.isUpdate = false;
        //checkVersion();
        if(show)checkVersionCommon();
        if(currentTabIndex == (int)SPUtils.get(this,SPValue.videoIndex,1)){
            EventBus.getDefault().post(new MainTabChange((int)SPUtils.get(this,SPValue.videoIndex,1)));
        }
    }

    private void checkVersionCommon() {
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode + "";
            CommonData.versionCode = Integer.parseInt(versionCode);
            HhLog.e("getVersion: versionCode " + versionCode);
        } catch (Exception e) {
            HhLog.e("getVersion: e " + e);
        }
        RequestParams params = new RequestParams(URLConstant.COMMON_VERSION);
        VersionBean versionBean = new VersionBean(1, 20, new VersionBean.DTO(null, null, null, CommonData.pushFlag));
        params.setBodyContent(new Gson().toJson(versionBean));
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("COMMON_VERSION" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        JSONObject obj = (JSONObject) data.get(0);
                        JSONArray dataList = obj.getJSONArray("dataList");
                        if(dataList.length()>0){
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject bean = (JSONObject) dataList.get(i);
                                if ("New".equals(bean.getString("status"))) {
                                    versionService = bean.getString("version");
                                    CommonData.versionCodeService = Integer.parseInt(versionService);
                                    versionNameService = bean.getString("versionName");
                                    String versionDescription = CommonUtil.parseContent(bean.getString("versionDescription"));
                                    String apkUrl = bean.getString("apkUrl");
                                    String isForce = bean.getString("isForce");
                                    if("null".equals(isForce)){isForce = "0";}
                                    HhLog.e("version " + versionService + versionNameService);
                                    EventBus.getDefault().post(new Update(apkUrl,200000,Integer.parseInt(versionService),versionNameService.replace("V",""),"68919BF998C29DA3F5BD2C0346281AC0",Integer.parseInt(isForce), versionDescription));

                                    return;
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    HhLog.e("error " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e("COMMON_VERSION error " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    String versionCode = "";
    String versionService = "";
    String versionNameService = "";
    private void checkVersion() {
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode + "";
            HhLog.e("getVersion: versionCode " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            HhLog.e("getVersion: e " + e);
        }
        RequestParams params = new RequestParams(URLConstant.GET_VERSION);
        HhHttp.getX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("version " + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        JSONObject object = jsonObject.getJSONArray("data").getJSONObject(0);
                        versionService = object.getString("version");
                        versionNameService = object.getString("versionName");
                        String versionDescription = object.getString("versionDescription");
                        String apkUrl = object.getString("apkUrl");
                        String isForce = object.getString("isForce");
                        HhLog.e("version " + versionService + versionNameService);
                        EventBus.getDefault().post(new Update(apkUrl,200000,Integer.parseInt(versionService),versionNameService.replace("V",""),"68919BF998C29DA3F5BD2C0346281AC0",Integer.parseInt(isForce),versionDescription));

                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    ///APK更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Update update) {
        update(update);
    }

    void update(Update update){
        if(update.getForceUpdateFlag()==0){
            show = false;
            new Handler().postDelayed(() -> {
                try{
                    show = true;
                }catch (Exception e){
                    HhLog.e(e.getMessage());
                }
            },10000);
        }
        CommonData.isUpdate = true;
        //第二种方式，使用MODEL方式，组装好对应的MODEL，传入sdk中
        DownloadInfo info = new DownloadInfo().setApkUrl(update.getApkUrl())
                //.setFileSize(update.getFileSize())
                .setProdVersionCode(update.getProdVersionCode())
                .setProdVersionName(update.getProdVersionName())
                .setMd5Check(update.getMd5Check())
                .setForceUpdateFlag(update.getForceUpdateFlag())
                .setUpdateLog(update.getUpdateLog());
        AppUpdateUtils.getInstance()
                .addMd5CheckListener(new MD5CheckListener() {
                    @Override
                    public void fileMd5CheckFail(String originMD5, String localMD5) {

                    }

                    @Override
                    public void fileMd5CheckSuccess() {

                    }
                })//添加MD5检查更新
                .addAppDownloadListener(new AppDownloadListener() {
                    @Override
                    public void downloading(int progress) {

                    }

                    @Override
                    public void downloadFail(String msg) {

                    }

                    @Override
                    public void downloadComplete(String path) {
                        CommonData.isUpdate = false;
                    }

                    @Override
                    public void downloadStart() {

                    }

                    @Override
                    public void reDownload() {

                    }

                    @Override
                    public void pause() {

                    }
                })//添加文件下载监听
                .checkUpdate(info);
    }

    ///Tab切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MainTabChange event) {
        index = event.getIndex();
        HhLog.e("index = " + index);

        if (currentTabIndex != this.index) {
            binding.botBar.setFirstSelectedPosition(index).initialise();
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[this.index].isAdded()) {
                trx.add(R.id.main_layout, fragments[this.index]);
            }
            trx.show(fragments[this.index]).commit();
        }
        currentTabIndex = this.index;
    }

    ///未读消息数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageChange event) {
        if(event.getMessage()==null || event.getMessage().isEmpty() || Objects.equals(event.getMessage(), "0")){
            textBadgeItem.hide();
            textBadgeItem.setText("");
        }else{
            textBadgeItem.show();
            textBadgeItem.setText(event.getMessage());
        }

    }

    ///推送透传更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(DoUpdate event) {
        //checkVersion();
        checkVersionCommon();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        CommonData.clear();
    }

    private void initBottomBar() {
        binding.botBar.setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 *///getResources().getColor(R.color.mainColor)
                .setActiveColor("#ffffff")//选中颜色
                .setInActiveColor("#000000")//未选中颜色
                .setBarBackgroundColor("#3472FF");//导航栏背景色
        binding.botBar.setMinimumHeight(30);
        textBadgeItem = new TextBadgeItem()
                // .setBorderWidth(0)
                // .setBackgroundColor(R.color.redColor)
                .setText("0")
                .setTextColorResource(R.color.whiteColor)
                //.setBorderColorResource(R.color.redColor)
                .setHideOnSelect(false);
        textBadgeItem.hide();

        messageBottomNavigationItem = new BottomNavigationItem(R.drawable.message_selected, "消息").setBadgeItem(textBadgeItem).setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.message));

        if(CommonData.hasMainApp)binding.botBar.addItem(new BottomNavigationItem(R.drawable.main_main, "首页").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.main_main_un)));
        if(CommonData.hasMainVideo)binding.botBar.addItem(new BottomNavigationItem(R.drawable.main_video, "视频").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.main_video_un)));
        if(CommonData.hasMainMessage)binding.botBar.addItem(messageBottomNavigationItem);
        if(CommonData.hasMainMap)binding.botBar.addItem(new BottomNavigationItem(R.drawable.main_map, "地图").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.main_map_un)));
        if(CommonData.hasMainMy)binding.botBar.addItem(new BottomNavigationItem(R.drawable.main_my, "我的").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.main_my_un)));

        binding.botBar.setFirstSelectedPosition(0)
                .initialise();//一定要放在 所有设置的最后一项

        BottomBarUtils.setBottomNavigationItem(binding.botBar, 10, 26, 13, this);

        if(CommonData.hasMainApp)mainFragment = MainFragment.newInstance("首页");
        if(CommonData.hasMainVideo)videoFragment = VideoFragment.newInstance("视频");
        if(CommonData.hasMainMessage)messageFragment = MessageFragment.newInstance("消息");
        if(CommonData.hasMainMap)mapFragment = MapFragment.newInstance("地图");
        if(CommonData.hasMainMy)mineFragment = MineFragment.newInstance("我的");
        fragments = new Fragment[]{/*mainFragment,
                videoFragment, messageFragment, mapFragment, mineFragment*/};
        if(CommonData.hasMainApp){
            Fragment[] fgs = new Fragment[this.fragments.length + 1];
            System.arraycopy(fragments,0,fgs,0,fragments.length);
            System.arraycopy( new Fragment[]{mainFragment},0,fgs,fragments.length,1);
            fragments = fgs;
        }
        if(CommonData.hasMainVideo){
            Fragment[] fgs = new Fragment[this.fragments.length + 1];
            System.arraycopy(fragments,0,fgs,0,fragments.length);
            System.arraycopy( new Fragment[]{videoFragment},0,fgs,fragments.length,1);
            fragments = fgs;
        }
        if(CommonData.hasMainMessage){
            Fragment[] fgs = new Fragment[this.fragments.length + 1];
            System.arraycopy(fragments,0,fgs,0,fragments.length);
            System.arraycopy( new Fragment[]{messageFragment},0,fgs,fragments.length,1);
            fragments = fgs;
        }
        if(CommonData.hasMainMap){
            Fragment[] fgs = new Fragment[this.fragments.length + 1];
            System.arraycopy(fragments,0,fgs,0,fragments.length);
            System.arraycopy( new Fragment[]{mapFragment},0,fgs,fragments.length,1);
            fragments = fgs;
        }
        if(CommonData.hasMainMy){
            Fragment[] fgs = new Fragment[this.fragments.length + 1];
            System.arraycopy(fragments,0,fgs,0,fragments.length);
            System.arraycopy( new Fragment[]{mineFragment},0,fgs,fragments.length,1);
            fragments = fgs;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if(CommonData.hasMainApp)fragmentTransaction.add(R.id.main_layout, mainFragment);
        if(CommonData.hasMainVideo)fragmentTransaction.add(R.id.main_layout, videoFragment);
        if(CommonData.hasMainMessage)fragmentTransaction.add(R.id.main_layout, messageFragment);
        if(CommonData.hasMainMap)fragmentTransaction.add(R.id.main_layout, mapFragment);
        if(CommonData.hasMainMy)fragmentTransaction.add(R.id.main_layout, mineFragment);
        if(CommonData.hasMainVideo)fragmentTransaction.hide(videoFragment);
        if(CommonData.hasMainMessage)fragmentTransaction.hide(messageFragment);
        if(CommonData.hasMainMap)fragmentTransaction.hide(mapFragment);
        if(CommonData.hasMainMy)fragmentTransaction.hide(mineFragment);
        if(CommonData.hasMainApp)fragmentTransaction.show(mainFragment);
        else if(CommonData.hasMainVideo)fragmentTransaction.show(videoFragment);
        else if(CommonData.hasMainMessage)fragmentTransaction.show(messageFragment);
        else if(CommonData.hasMainMap)fragmentTransaction.show(mapFragment);
        else fragmentTransaction.show(mineFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected ActivityMainBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_main);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MainViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MainViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();
        CommonData.mainTabIndex = position;
        EventBus.getDefault().post(new MainTabChange(position));
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                index = 0;
                break;
            case 1:
                index = 1;

                /*if (SPUtils.get(MainActivity.this, SPValue.token, "") != "") {
                    initBottom();
                } else {
                    Log.e("qc", "onTabSelected: denglu" );
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.botBar.selectTab(0);
                }*/
                break;
            case 2:
                index = 2;
                /*if (SPUtils.get(MainActivity.this, SPValue.token, "") != "") {
                    initBottom();
                } else {
                    Log.e("qc", "onTabSelected: denglu" );
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.botBar.selectTab(0);
                }*/

                break;
            case 3:
                index = 3;
                /*if (SPUtils.get(MainActivity.this, SPValue.token, "") != "") {
                    initBottom();
                } else {
                    Log.e("qc", "onTabSelected: denglu" );
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.botBar.selectTab(0);
                }*/

                break;
            case 4:
                index = 4;
                /*if (SPUtils.get(MainActivity.this, SPValue.token, "") != "") {
                    initBottom();
                } else {
                    Log.e("qc", "onTabSelected: denglu" );
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.botBar.selectTab(0);
                }*/

                break;

            default:
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.main_layout, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;

        //  transaction.commit();// 事务提交
    }

    /**
     * 设置未选中的Fragment事务
     * @param position
     */
    @Override
    public void onTabUnselected(int position) {

    }

    /**
     *  设置释放Fragment事务
     * @param position
     */
    @Override
    public void onTabReselected(int position) {

    }

    private void requestNotificationPermission() {
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
        //通知栏权限
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean enabled = isNotificationEnabled(MainActivity.this);

                Log.e("TAG", "requestNotificationPermission: " + enabled );
                if (!enabled) {
                    /**
                     * 跳到通知栏设置界面
                     * @param context
                     */
                    Intent localIntent = new Intent();
                    //直接跳转到应用通知设置的代码：
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        localIntent.putExtra("app_package", MainActivity.this.getPackageName());
                        localIntent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        localIntent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                    } else {
                        //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            localIntent.setAction(Intent.ACTION_VIEW);
                            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                            localIntent.putExtra("com.android.settings.ApplicationPkgName", MainActivity.this.getPackageName());
                        }
                    }
                    MainActivity.this.startActivity(localIntent);
                }
            }
        },3000);

        //自启动权限
        if(isIgnoringBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }
    }

    private void jumpNotification() {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", getPackageName());
            localIntent.putExtra("app_uid", getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
            }
        }
        startActivity(localIntent);
    }

    /**
     * 下面俩是申请后台运行的
     * @return
     */
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            }
        }
        return isIgnoring;
    }

    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取通知权限
     * @param context
     */
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if(currentTabIndex == 0){
            exit();
        }else{
            EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(this,SPValue.appIndex,0)));
        }

    }

    private boolean isExit = false;
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次回到主页", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        } else {
            finish();
        }
    }
}