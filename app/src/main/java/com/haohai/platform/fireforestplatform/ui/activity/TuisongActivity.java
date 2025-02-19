package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityLaunchBinding;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LaunchViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class TuisongActivity extends BaseLiveActivity<ActivityLaunchBinding, LaunchViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(!CommonData.token.isEmpty()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return;
        }*/

        permissions();
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
                        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
                        CommonData.hasMainApp = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainApp, true);
                        CommonData.hasMainVideo = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainVideo, false);
                        CommonData.hasMainMessage = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMessage, true);
                        CommonData.hasMainMap = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMap, true);
                        CommonData.hasMainMy = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMy, true);//强制显示'我的'菜单
                        if(login!=null && (boolean)login){
                            CommonData.token = (String) SPUtils.get(HhApplication.getInstance(), SPValue.token, "");
                            String userName = (String) SPUtils.get(HhApplication.getInstance(), SPValue.userName, "");
                            String password = (String) SPUtils.get(HhApplication.getInstance(), SPValue.password, "");

                            obtainViewModel().login(userName,password);
                        }else{
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(TuisongActivity.this, LoginActivity.class));
                                finish();
                            },2000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(CommonData.mainTabIndex == 1){
            EventBus.getDefault().post(new MainTabChange(1));
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