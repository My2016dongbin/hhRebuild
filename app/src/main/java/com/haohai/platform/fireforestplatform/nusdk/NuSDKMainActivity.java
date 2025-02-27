package com.haohai.platform.fireforestplatform.nusdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.genew.base.utils.ToastUtils;
import com.genew.mpublic.bean.auth.event.LogoutEvent;
import com.genew.mpublic.bean.xmpp.event.ReceiverMessageEvent;
import com.genew.mpublic.bean.xmpp.event.UnreadMessageCountUpdateEvent;
import com.genew.mpublic.router.Router;
import com.genew.mpublic.router.api.Api;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NuSDKMainActivity extends BaseActivity implements View.OnClickListener
{
    private final int REQUEST_LOGOUT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        blackText();
        setContentView(R.layout.activity_nusdk_main_new);

        if (Build.VERSION.SDK_INT >= 21) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.white));//cadetblue
        }
        init();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void init()
    {
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setOnClickListener(R.id.demo_call);
        setOnClickListener(R.id.demo_gmeet_audio);
        setOnClickListener(R.id.demo_gmeet_video);
        setOnClickListener(R.id.demo_address);

        /*versionNameTV = (TextView)findViewById(R.id.version_name);
        versionNameTV.setText(NuSDKDemoUtils.getVersionName(NuSDKMainActivity.this));*/
        //设置电话为窗口模式
    }

    private void setOnClickListener(int id)
    {
        findViewById(id).setOnClickListener(NuSDKMainActivity.this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            //打开电话界面
            case R.id.demo_call:
                if (Api.getApPhoneUi() != null) {
                    Api.getApPhoneUi().startDialActivity(this);
                } else {
                    ToastUtils.xxxdo("该功能示例暂未提供！");
                }
                break;
            //打开通讯录界面
            case R.id.demo_address:
                Intent addressIntent = new Intent(NuSDKMainActivity.this, AddressBookActivity.class);
                startActivity(addressIntent);
                break;
		case R.id.demo_gmeet_audio:
			//Router.startVideoConferenceGMeetActivity(null,null);
            //会议
            if (Api.getAudioConferenceUi() != null) {
                Api.getAudioConferenceUi().quickStartAudioConferenceActivity(this);
            } else {
                ToastUtils.xxxdo("该功能示例暂未提供！");
            }
            break;
		case R.id.demo_gmeet_video:
			//Router.startVideoConferenceGMeetActivity(null,null);
            //视频会议
            if (Api.getVideoConferenceUi() != null) {
                Api.getVideoConferenceUi().quickStartVideoConferenceActivity(this);
            } else {
                ToastUtils.xxxdo("该功能示例暂未提供！");
            }

            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGOUT)
        {    // 注销
            if (resultCode == RESULT_OK)
            {
                Intent intent = new Intent(NuSDKMainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event)
    {
        finish();
    }


    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
