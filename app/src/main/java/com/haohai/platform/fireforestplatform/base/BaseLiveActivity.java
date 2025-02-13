package com.haohai.platform.fireforestplatform.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.HhToast;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

public abstract class BaseLiveActivity<T extends ViewDataBinding, V extends BaseViewModel> extends BaseActivity {
    protected T binding;

      private TokenFailureBroadcast mBroadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = dataBinding();
        setupViewModel();
        subscribeObserver();
        setupToolbar();


        //设置主题色白色 黑字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteColor));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //动态接收广播
        IntentFilter intentFilter = new IntentFilter();
        mBroadcast = new TokenFailureBroadcast();
        intentFilter.addAction(SPValue.TOKEN_FAILURE);
        registerReceiver(mBroadcast, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        HhToast.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //注销广播
       // unregisterReceiver(mBroadcast);
    }

    /**
     * 绑定视图
     *
     * @return
     */
    protected abstract T dataBinding();

    /**
     * 绑定ViewModel
     */
    protected abstract void setupViewModel();

    /**
     * 注册ViewModel监听
     */
    protected void subscribeObserver() {
        obtainViewModel().msg.observe(this, msg -> {
            //关闭软键盘
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(binding.getRoot().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            Log.e("TAG", "subscribeObserver: " + msg );
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        //token失效
        obtainViewModel().loading.observe(this, dialogEvent -> {
            if (dialogEvent.isShow()) {
                DialogHelper.getInstance().show(this, dialogEvent.getHint());
            } else {
                DialogHelper.getInstance().close();
            }
        });
    }

    /**
     * 设置标题栏
     */
    protected void setupToolbar() {

    }

    public abstract V obtainViewModel();


    /**
     * 登录信息失效广播
     * 广播接收者
     */
    public class TokenFailureBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            HhLog.e("hh","登录失效");
            SPUtils.clear(getApplicationContext());
        }
    }

}
