package com.haohai.platform.fireforestplatform;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

//import com.alibaba.android.arouter.launcher.ARouter;
import androidx.annotation.RequiresApi;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.model.UpdateConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.cretin.www.cretinautoupdatelibrary.utils.SSLUtils;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.OkHttp3Connection;
import com.huamai.poc.IPocEngineEventHandler;
import com.huamai.poc.PocEngine;
import com.huamai.poc.PocEngineFactory;
import com.huamai.poc.chat.ChatMessageCategory;
import com.huamai.poc.chat.ChatMessageStatus;
import com.huamai.poc.greendao.ChatMessage;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.IOSStyle;
import com.kongzue.dialogx.style.MaterialStyle;
import com.kongzue.dialogx.util.InputInfo;
import com.kongzue.dialogx.util.TextInfo;
import com.qweather.sdk.view.HeConfig;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.unionbroad.app.eventbus.ChannelChangedEvent;
import com.unionbroad.app.util.FileDownloadManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;
import okhttp3.OkHttpClient;

public class HhApplication extends Application {
    private boolean isLoadTBS = false; // 是否成功加载

    private static volatile HhApplication instance;
    public static final String APP_FOLDER_NAME = "hhBaiduGuide";
    public static final String TAG = "HhApplication";

    public static HhApplication getInstance() {
        if (instance == null) {
            synchronized (HhApplication.class) {
                if (instance == null) {
                    instance = new HhApplication();
                }
            }
        }
        return instance;
    }


    private PocEngine.Configure getConfig() {
        PocEngine.Configure configure = PocEngineConfigureProducer.getConfig();
        /** 可选配置 */
        //定位模式
        configure.gpsMode = IPocEngineEventHandler.GpsMode.NET_AND_GPS;
        //位置上报时间间隔，单位秒
        configure.gpsReportInterval = 300;
        //联系人状态刷新时间间隔，单位秒
        configure.statusUpdateInterval = 60;
        //使用音乐通道
        configure.useMusicStream = false;
        //对讲录音
        configure.pttPlayback = false;
        //防杀服务
        configure.keepLiveService = false;
        //视频通话分辨率，需要和调度台中设置的值一样才能生效
        configure.videoResolution = IPocEngineEventHandler.VideoResolution.RESOLUTION_640X480;
        configure.disableInternalGpsFunc = true;
        configure.hardwareEncode = false;
        configure.videoRenderMode = IPocEngineEventHandler.VideoViewRenderMode.RENDER_MODE_ASPECT_FIT;
        //禁用视频通话-画面时间水印
        configure.videoTimeOsdEnable = false;

        //缓存提供器，没特殊要求用不到
        //configure.cacheFileSupplier = cacheFileSupplier;
        //侧边功能键注册，一般都用不到
        //configure.broadcastHotKeyActionSupplier = hotKeyActionSupplier;
        //其它...
        return configure;
    }

    private SDKReceiver mReceiver;

    private void initBroadcast() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    //定位模块状态通知
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            HhLog.e("SDKReceiver: " + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                HhLog.e("key 验证出错! 错误码");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                HhLog.e("key 验证成功! 功能可以正常使用");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                HhLog.e("网络出错");
            }
        }
    }
    /**
     * 目前配置项较多，很多配置没有单独的设置接口，建议全局保持一个Configure，
     * 确保每次 PocEngineFactory.get().config()时，设置进去的配置项都是有效值
     */
    public static class PocEngineConfigureProducer {

        static PocEngine.Configure configure;

        public static PocEngine.Configure getConfig() {
            if (configure == null) {
                configure = new PocEngine.Configure();
                /** 必须设置 */
                configure.ip = "123.57.6.84";
                configure.port = "5060";
                configure.httpPort = "80";

                configure.ip = "39.106.29.203";
                configure.port = "6050";
                configure.httpPort = "80";
            }
            return configure;
        }
    }

    private final IPocEngineEventHandler pocEventHandler = new IPocEngineEventHandler() {

        @Override
        public void onMessageReceived(long chatId, List<ChatMessage> messages) {
            //收到新的消息
            if (messages != null && messages.size() > 0) {
                //仅为调试，只取出其中一条
                ChatMessage message = messages.get(0);
                //语音通话记录
                if (message.getCategory() == ChatMessageCategory.AUDIO) {
                    HhLog.e("==> 语音通话记录");
                }
                //视频通话记录
                else if (message.getCategory() == ChatMessageCategory.VIDEO) {
                    HhLog.e("==> 视频通话记录");
                }
                //语音广播
                else if (message.getCategory() == ChatMessageCategory.AUDIO_BROADCAST) {
                    HhLog.e("==> 语音广播");
                }
                //语音文件，类似微信的语音消息
                else if (message.getCategory() == ChatMessageCategory.AUDIO_FILE) {
                    HhLog.e("==> 语音消息");
                }
                //对讲录音，只有开启对讲回放功能，每次有人讲话时，才会把声音录制下来
                else if (message.getCategory() == ChatMessageCategory.PTT_AUDIO_FILE) {
                    HhLog.e("==> 对讲录音消息 " + message.getRemote_number() + " "
                            + message.getRemote_name() + " isOut=" + message.getIs_out());
                }
                //视频消息，类似微信的视频消息
                else if (message.getCategory() == ChatMessageCategory.VIDEO_FILE) {
                    HhLog.e("==> 视频消息: " + message.getDownload_status());
                    switch (message.getDownload_status()) {
                        case ChatMessageStatus.File.DOWN_SUCCESSED:
                            message.setDownload_status(ChatMessageStatus.File.DOWN_SUCCESSED);
                            //play
                            break;
                        case ChatMessageStatus.File.DOWN_UNINIT:
                            message.setDownload_status(ChatMessageStatus.File.DOWN_STARTING);
                            FileDownloadManager.getInstance().startDownTask(message.getId(), message.getHttpFile(), message.getLocalFile());
                            //使用EventBus监听ChatFileDownloadCompletedEvent，获取文件下载结果
                            break;
                    }
                }
                //图片消息
                else if (message.getCategory() == ChatMessageCategory.IMAGE) {
                    HhLog.e("==> 图片消息");
                }
                //位置消息
                else if (message.getCategory() == ChatMessageCategory.LOCATION) {
                    HhLog.e("==> 位置消息");
                }
                //文字消息
                else if (message.getCategory() == ChatMessageCategory.TEXT) {
                    HhLog.e("==> 文字消息: " + message.getText());
                }

                //================以下两种消息，如果业务需求，可以放到动态页中去=========================
                //拍传@消息
                else if (message.getCategory() == ChatMessageCategory.NOTIFICATION_REPORT) {
                    HhLog.e("==> 拍传@消息");
                }
                //报警消息
                else if (message.getCategory() == ChatMessageCategory.ALERT) {
                    HhLog.e("==> 报警消息: " + message.getText());
                }
                //任务消息
                else if (message.getCategory() == ChatMessageCategory.NOTIFICATION_TASK) {
                    HhLog.e("==> 任务消息: " + message.getText());
                }
                //==================================================================================

                //其它消息一般用不到
                else {
                    HhLog.e("==> 其它消息: " + message.getText());
                }
            }
        }

        @Override
        public void onIncoming(IncomingInfo incomingInfo) {
            HhLog.e("onIncoming-> sessionId=" + incomingInfo.sessionId + " type=" + incomingInfo.sessionType + " uid=" +
                    incomingInfo.callerId + " name=" + incomingInfo.callerName + " level=" + incomingInfo.level +
                    " extra=" + incomingInfo.extra);
            Intent intent = new Intent(HhApplication.this, LoginActivity.class);//TODO AvActivity.class
            intent.putExtra("sessionId", incomingInfo.sessionId);
            intent.putExtra("callerId", incomingInfo.callerId);
            intent.putExtra("callerName", incomingInfo.callerName);
            intent.putExtra("type", incomingInfo.sessionType);
            intent.putExtra("extra", incomingInfo.extra);
            intent.putExtra("isIncomingCall", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HhApplication.this.startActivity(intent);
        }

        @Override
        public void onIgnoreIncoming(int sessionType, long callerId) {
            super.onIgnoreIncoming(sessionType, callerId);
        }

        @Override
        public void onChannelChangedEvent(ChannelChangedEvent event) {
            Toast.makeText(getApplicationContext(), "onChannelChangedEvent", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onForcedOffline() {
            //True: 响应了下线事件，False：没有响应，sdk内部会弹出对话框，禁止再操作
            Toast.makeText(getApplicationContext(), "onForcedOffline", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onBackstageMonitorAction(int action, long sessionId, String monitorUid, boolean isVideoMonitor) {
            HhLog.e("onBackstageMonitorAction: action=" + action);
        }

        @Override
        public void onReceiveLocation(double latitude, double longitude, String address) {
            Toast.makeText(getApplicationContext(), "坐标变化(" + latitude + "," + longitude + ")", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onRequestCamera() {
            Toast.makeText(getApplicationContext(), "请求Camera", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReleaseCamera() {
            Toast.makeText(getApplicationContext(), "释放Camera", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestMic() {
            Toast.makeText(getApplicationContext(), "请求Mic", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReleaseMic() {
            Toast.makeText(getApplicationContext(), "释放Mic", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoStreamTakePictureFinish(String filePath) {
            Toast.makeText(getApplicationContext(), "已保存到: " + filePath, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallConnected(long sessionId, String remoteId, int sessionType) {
        }

    };
    private String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;


        /** 防止反复初始化 */
        if (getPackageName() != null && getPackageName().equals(getProcessName(android.os.Process.myPid()))) {
            initBroadcast();
            PocEngineFactory.initialize(this, getConfig());
            //可以在Application中监听，也可以在Service中监听，主要希望全局监听来电和新消息
            PocEngineFactory.get().addEventHandler(pocEventHandler);
        }

        //ARouter.init(this); //ARouter初始化


        // MUST use app context to avoid memory leak!
        // load with glide 大图预览
        BigImageViewer.initialize(GlideImageLoader.with(this));


        // 初始化AutoSize
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);

        //初始化xUtils
        x.Ext.init(this);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(new AllowAllHostnameVerifier())
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);


        //如果你想使用okhttp作为下载的载体，那么你需要自己依赖okhttp，更新库不强制依赖okhttp！可以使用如下代码创建一个OkHttpClient 并在UpdateConfig中配置setCustomDownloadConnectionCreator start
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30_000, TimeUnit.SECONDS)
                .readTimeout(30_000, TimeUnit.SECONDS)
                .writeTimeout(30_000, TimeUnit.SECONDS)
                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 start
                .sslSocketFactory(SSLUtils.createSSLSocketFactory())
                .hostnameVerifier(new SSLUtils.TrustAllHostnameVerifier())
                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 end
                .retryOnConnectionFailure(true);
        //如果你想使用okhttp作为下载的载体，那么你需要自己依赖okhttp，更新库不强制依赖okhttp！可以使用如下代码创建一个OkHttpClient 并在UpdateConfig中配置setCustomDownloadConnectionCreator end
        //APK升级-当你希望使用传入model的方式，让插件自己解析并实现更新
        UpdateConfig updateConfig = new UpdateConfig()
                .setDebug(true)//是否是Debug模式
                .setCustomDownloadConnectionCreator(new OkHttp3Connection.Creator(builder))
                .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL)//设置获取更新信息的方式
                .setShowNotification(false)//配置更新的过程中是否在通知栏显示进度
                .setNotificationIconRes(R.drawable.ic_icon)//配置通知栏显示的图标
                .setUiThemeType(TypeConfig.UI_THEME_G)//配置UI的样式，一种有12种样式可供选择
                .setAutoDownloadBackground(false)//是否需要后台静默下载，如果设置为true，则调用checkUpdate方法之后会直接下载安装，不会弹出更新页面。当你选择UI样式为TypeConfig.UI_THEME_CUSTOM，静默安装失效，您需要在自定义的Activity中自主实现静默下载，使用这种方式的时候建议setShowNotification(false)，这样基本上用户就会对下载无感知了
                //.setCustomActivityClass(CustomActivity.class)//如果你选择的UI样式为TypeConfig.UI_THEME_CUSTOM，那么你需要自定义一个Activity继承自RootActivity，并参照demo实现功能，在此处填写自定义Activity的class
                .setNeedFileMD5Check(false)//是否需要进行文件的MD5检验，如果开启需要提供文件本身正确的MD5校验码，DEMO中提供了获取文件MD5检验码的工具页面，也提供了加密工具类Md5Utils
                //.setCustomDownloadConnectionCreator(new OkHttp3Connection.Creator(builder));//如果你想使用okhttp作为下载的载体，可以使用如下代码创建一个OkHttpClient，并使用demo中提供的OkHttp3Connection构建一个ConnectionCreator传入，在这里可以配置信任所有的证书，可解决根证书不被信任导致无法下载apk的问题
                ;
        AppUpdateUtils.init(this, updateConfig);


        //和风天气初始化
        HeConfig.init("HE2204290853031335", "bfa67b447fbd4c0f949c95c789d531af");
        //切换至开发版服务
        HeConfig.switchToDevService();

        //baiduMap
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //qbSdkInit;
        if (isLoadTBS) { // 如果已经成功加载过，就不必重复加载了
            return;
        }
        QbSdk.setDownloadWithoutWifi(true); //非WiFi情况下，主动下载TBS内核
        // 搜集本地TBS内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean isX5Core) {
                isLoadTBS = isX5Core; // 为true表示内核加载成功
                HhLog.e("TBS内核" + isLoadTBS);
            }

            @Override
            public void onCoreInitFinished() {}
        };
        // TBS内核初始化
        QbSdk.initX5Environment(getApplicationContext(), cb);
        // 以下设置会将Dex文件转为Oat的过程加以优化
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map); // 初始化TBS设置

        //initDialogX;
        //初始化
        DialogX.init(this);

        //开启调试模式，在部分情况下会使用 Log 输出日志信息
        DialogX.DEBUGMODE = true;

        //设置主题样式
        //DialogX.globalStyle = MaterialStyle.style();
        DialogX.globalStyle = IOSStyle.style();

        //设置亮色/暗色（在启动下一个对话框时生效）
        DialogX.globalTheme = DialogX.THEME.LIGHT;

        //设置对话框最大宽度（单位为像素）
        DialogX.dialogMaxWidth = 1920;

        //设置 InputDialog 自动弹出键盘
        DialogX.autoShowInputKeyboard = true;

        //限制 PopTip 一次只显示一个实例（关闭后可以同时弹出多个 PopTip）
        DialogX.onlyOnePopTip = true;

        TextInfo textInfo = new TextInfo();
        InputInfo inputInfo = new InputInfo();

        //设置对话框默认按钮文本字体样式
        DialogX.buttonTextInfo = (textInfo);

        //设置对话框默认确定按钮文字样式
        DialogX.okButtonTextInfo = (textInfo);

        //设置对话框默认标题文字样式
        DialogX.titleTextInfo = (textInfo);

        //设置对话框默认内容文字样式
        DialogX.messageTextInfo = (textInfo);

        //设置默认 WaitDialog 和 TipDialog 文字样式
        DialogX.tipTextInfo = (textInfo);

        //设置默认输入框文字样式
        DialogX.inputInfo = (inputInfo);

        //设置默认底部菜单、对话框的标题文字样式
        DialogX.menuTitleInfo = (textInfo);

        //设置默认底部菜单文本样式
        DialogX.menuTextInfo = (textInfo);

        //设置默认对话框背景颜色（值为ColorInt，为-1不生效）
        DialogX.backgroundColor = Color.WHITE;

        //设置默认对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
        DialogX.cancelable = true;

        //设置默认提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
        DialogX.cancelableTipDialog = true;

        //设置默认取消按钮文本文字，影响 BottomDialog
        DialogX.cancelButtonText = ("取消");

        //设置默认 PopTip 文本样式
        DialogX.popTextInfo = (textInfo);

        //设置全局 Dialog 生命周期监听器
        //DialogX.dialogLifeCycleListener = (DialogLifecycleCallback);

        //设置 TipDialog 和 WaitDialog 明暗风格，不设置则默认根据 globalTheme 定义
        //DialogX.tipTheme = (THEME);

        //默认 TipDialog 和 WaitDialog 背景颜色（值为 ColorInt，为-1不生效）
        //DialogX.tipBackgroundColor = (ColorInt)

        /**
         * 重写 TipDialog 和 WaitDialog 进度动画颜色，
         * 注意此属性为覆盖性质，即设置此值将替换提示框原本的进度动画的颜色，包括亮暗色切换的颜色变化也将被替代
         * （值为 ColorInt，为-1不生效）
         */
        //DialogX.tipProgressColor = (ColorInt)

        /**
         * 设置 BottomDialog 导航栏背景颜色
         */
        DialogX.bottomDialogNavbarColor = Color.TRANSPARENT;

        //是否自动在主线程执行
        DialogX.autoRunOnUIThread = true;


        initTPNS();

    }

    private void initTPNS() {
        //腾讯推送
        XGPushConfig.enableDebug(this,true);
        //开启小米推送
        XGPushConfig.setMiPushAppId(getApplicationContext(), "2882303761518845436");
        XGPushConfig.setMiPushAppKey(getApplicationContext(), "5981884523436");

        //oppo推送
        XGPushConfig.setOppoPushAppId(getApplicationContext(), "93a4e3e3bcb3448387c2af2eadc1f4ee");
        XGPushConfig.setOppoPushAppKey(getApplicationContext(), "e317f095327f4ca0bf3f2837f9fece03");

        //打开第三方推送  华为推送
        XGPushConfig.enableOtherPush(getApplicationContext(), true);


        //设置魅族APPID和APPKEY
        XGPushConfig.setMzPushAppId(this, "136981");
        XGPushConfig.setMzPushAppKey(this, "ae5633cd5a4146a1832b7cc3caa4d205");

        //开启华为推送
        XGPushConfig.enableOtherPush(getApplicationContext(), true);
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
}
