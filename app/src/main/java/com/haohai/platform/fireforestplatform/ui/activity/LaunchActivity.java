package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.Utils;
import com.genew.base.net.base.OnRequestResultListener;
import com.genew.base.net.bean.NiuxinLoginInfo;
import com.genew.base.net.bean.NiuxinResultInfo;
import com.genew.base.utils.ToastUtils;
import com.genew.mpublic.router.api.Api;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.ActivityLaunchBinding;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.nusdk.ErrorCode;
import com.haohai.platform.fireforestplatform.nusdk.permission.PermissionListener;
import com.haohai.platform.fireforestplatform.nusdk.permission.PermissionsUtil;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LaunchViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.koushikdutta.ion.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LaunchActivity extends BaseLiveActivity<ActivityLaunchBinding, LaunchViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(!CommonData.token.isEmpty()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return;
        }*/

        checkPermissionAndLoginNuSDK();

        permissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(CommonData.mainTabIndex == (Integer) SPUtils.get(this,SPValue.videoIndex,1)){
            EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(this,SPValue.videoIndex,1)));
        }

        checkPermissionAndLoginNuSDK();

        permissions();
    }

    OnRequestResultListener onLoginResultListener = new OnRequestResultListener()
    {

        @Override
        public void onResult(Response<String> response, NiuxinResultInfo info)
        {
            // 登录出现异常错误
            if (info == null)
            {
                runOnUiThread(() ->
                {
                    ToastUtils.xxxif(R.string.toast_login_fail_by_network);
                });
            } else if (response != null)
            {
                NiuxinLoginInfo loginInfo = (NiuxinLoginInfo) info;

                if (loginInfo.isLoginSuccessed())
                {
                    //TODO 测试 登录成功 主页跳转
                    /*startActivity(new Intent(LaunchActivity.this,
                            MainActivity.class));
                    finish();*/
                    if(!CommonData.token.isEmpty()){
                        startActivity(new Intent(LaunchActivity.this,MainActivity.class));
                    }/*else{
                        startActivity(new Intent(LaunchActivity.this,
                                LoginActivity.class));
                    }*/
                } else
                {
                    runOnUiThread(() ->
                    {
                        String error = ErrorCode.getNiuxinErrorString(
                                loginInfo.status.code);
                        //ToastUtils.xxxdo(getString(R.string.toast_login_fail_by_code) + error);
                    });
                }
                new Handler().postDelayed(() -> finish(),5000);
            }
        }
    };
    private void checkPermissionAndLoginNuSDK()
    {
        final boolean needsResetLogback;
        needsResetLogback = !PermissionsUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionsUtil.requestNecessaryPermission(new PermissionListener()
        {
            @Override
            public void permissionGranted(
                    @NonNull String[] permission)
            {
                if(needsResetLogback)
                {
                    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                    loggerContext.reset();
                    ContextInitializer ci = new ContextInitializer(loggerContext);
                    try
                    {
                        ci.autoConfig();
                    } catch (JoranException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(Settings.System.canWrite(Utils.getApp()))
                    {
                        loginNuSDK();
                    }else
                    {
                        //再次申请
                        PermissionsUtil.requestWriteSettingsPermission(LaunchActivity.this,null);
                    }
                }else
                {
                    loginNuSDK();
                }
            }

            @Override
            public void permissionDenied(
                    @NonNull String[] permission)
            {
                com.blankj.utilcode.util.ToastUtils.showShort(getString(R.string.auth_must_grant_necessary_permission));
            }
        });
    }

    private void loginNuSDK() {
        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
        if(login!=null && (boolean)login){
            String phone = (String) SPUtils.get(HhApplication.getInstance(), SPValue.phone, "");
//            phone = "yunwei18";//TODO 测试环境test
            //NuSDK融合通信登录
            Api.getApiAuth().saveServerUrlData(URLConstant.NU_SDK_IP, URLConstant.NU_SDK_PORT, "", "", false);
            Api.getApiAuth().login(phone, URLConstant.NU_SDK_PASSWORD, Api.getApiAuth().getImeiNum(phone),
                    true, onLoginResultListener);
        }/*else{
            new Handler().postDelayed(() -> {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                finish();
            },2000);
        }*/
    }

    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        HhLog.e("permissions " + b);
                        goNext_();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void goNext_() {
        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
        CommonData.hasMainApp = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainApp, false);
        CommonData.hasMainVideo = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainVideo, false);
        CommonData.hasMainMessage = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMessage, false);
        CommonData.hasMainMap = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMap, false);
        CommonData.hasMainMy = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMy, true);//强制显示'我的'菜单
        if(login!=null && (boolean)login){
            CommonData.token = (String) SPUtils.get(HhApplication.getInstance(), SPValue.token, "");
            String userName = (String) SPUtils.get(HhApplication.getInstance(), SPValue.userName, "");
            String password = (String) SPUtils.get(HhApplication.getInstance(), SPValue.password, "");

            obtainViewModel().login(userName,password);
        }else{
            new Handler().postDelayed(() -> {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                finish();
            },2000);
        }
    }

    @Override
    protected ActivityLaunchBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_launch);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LaunchViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LaunchViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}