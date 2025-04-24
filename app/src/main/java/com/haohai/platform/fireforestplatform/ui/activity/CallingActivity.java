package com.haohai.platform.fireforestplatform.ui.activity;

import static com.netease.lava.nertc.sdk.video.NERtcVideoStreamType.kNERtcVideoStreamTypeMain;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityCallingBinding;
import com.haohai.platform.fireforestplatform.event.Join;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.VideoChat;
import com.haohai.platform.fireforestplatform.ui.multitype.VideoChatViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.CallingViewModel;
import com.haohai.platform.fireforestplatform.utils.Action;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.NERtcUserJoinExtraInfo;
import com.netease.lava.nertc.sdk.NERtcUserLeaveExtraInfo;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import me.drakeet.multitype.MultiTypeAdapter;

public class CallingActivity extends BaseLiveActivity<ActivityCallingBinding, CallingViewModel> implements NERtcCallback, VideoChatViewBinder.OnItemClickListener {

    private final String TAG = CallingActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        obtainViewModel().audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        init_();
        bind_();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        try {
            obtainViewModel().closeSpeaker();
        }catch (Exception e){
            //
        }
        NERtcEx.getInstance().leaveChannel();
        NERtcEx.getInstance().release();// 销毁实例
        super.onDestroy();
    }

    ///加入房间(对方加入频道)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Join join) {
        HhLog.e("onGetMessage Join");
//        EventBus.getDefault().post(WechatBus.getInstance(true));//关闭铃声
        joinChannelXD();//加入信令频道
        obtainViewModel().joinRoom(CommonData.audioRoomName);//加入语音房间

        changeVisible();
    }

    private void changeVisible() {
        binding.flCalling.setVisibility(View.GONE);
        binding.llSpeaking.setVisibility(View.VISIBLE);
        //以开启本地视频主流采集并发送为例
        NERtcEx.getInstance().enableLocalVideo(kNERtcVideoStreamTypeMain,true);
    }


    private void joinChannelXD() {
        //Toast.makeText(this, "准备加入频道id " + CommonData.xdChannelId + "," + CommonData.testId, Toast.LENGTH_LONG).show();
        long selfUid = Long.parseLong((String) SPUtils.get(this, SPValue.phone, ""));
        NIMClient.getService(SignallingService.class).join(obtainViewModel().cId, selfUid, "", false).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {
                    @Override
                    public void onResult(int i, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        if (i == ResponseCode.RES_SUCCESS) {
                            Toast.makeText(CallingActivity.this, "加入频道成功", Toast.LENGTH_SHORT).show();
                        } else if (i == ResponseCode.RES_CHANNEL_MEMBER_HAS_EXISTS) {
                            Toast.makeText(CallingActivity.this, "已经在频道中", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CallingActivity.this, "加入频道失败 code=" + i, Toast.LENGTH_SHORT).show();
                            HhLog.e("obtainViewModel().channelId = " + obtainViewModel().channelId + " , selfUid = " + selfUid);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void init_() {
        binding.topBar.title.setText("视频会议");

        binding.flCalling.setVisibility(View.VISIBLE);
        binding.llSpeaking.setVisibility(View.GONE);
        //初始化网易云信
        stupNERtc();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.videoList.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.videoList.setHasFixedSize(true);
        binding.videoList.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        VideoChatViewBinder videoChatViewBinder = new VideoChatViewBinder(this);
        videoChatViewBinder.setListener(this);
        obtainViewModel().adapter.register(VideoChat.class, videoChatViewBinder);
        binding.videoList.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.videoList, obtainViewModel().adapter);


        Intent intent = getIntent();
        obtainViewModel().isCalling = intent.getBooleanExtra("isCalling",false);
        if(!obtainViewModel().isCalling){
            //被邀请
            obtainViewModel().channelId = intent.getStringExtra("channelId");
            obtainViewModel().accountId = intent.getStringExtra("accountId");
            obtainViewModel().requestId = intent.getStringExtra("requestId");
            obtainViewModel().customInfo = intent.getStringExtra("customInfo");
            try {
                JSONObject jsonObject = new JSONObject(obtainViewModel().customInfo);
                String name = jsonObject.getString("name");
                binding.callName.setText(name);
                binding.callStatus.setText("邀请您视频通话...");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            obtainViewModel().invite();
            binding.callingOpen.setVisibility(View.GONE);
            binding.callingCloseText.setVisibility(View.GONE);
            binding.callingFlSpace.setVisibility(View.GONE);
            binding.callName.setText("被邀请人");
            binding.callStatus.setText("正在发起视频通话...");
        }
    }

    private void bind_() {
        CommonUtil.click(binding.callingOpen, new Action() {
            @Override
            public void click() {
                obtainViewModel().acceptInvite();
                changeVisible();
            }
        });
        CommonUtil.click(binding.callingClose, new Action() {
            @Override
            public void click() {
                if(obtainViewModel().isCalling){
                    //取消邀请
                    obtainViewModel().cancelInviteOther();
                }else{
                    //拒绝
                    obtainViewModel().rejectInvite("",CommonData.invitedEvent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1000);
                }
            }
        });
        CommonUtil.click(binding.speakingClose, new Action() {
            @Override
            public void click() {
                finish();
            }
        });
        CommonUtil.click(binding.audio, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.video, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.person, new Action() {
            @Override
            public void click() {

            }
        });
    }


    private void localVideoShow() {
        NERtcEx.getInstance().setupLocalVideoCanvas(binding.topVideo);
        NERtcEx.getInstance().startVideoPreview(kNERtcVideoStreamTypeMain);
        //设置本地视频画面的渲染模式：以保证原始视频尺寸比例为例（可选）
        binding.topVideo.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        //设置本地视频画面的镜像模式：以开启镜像为例（可选）
        binding.topVideo.setMirror(true);
    }

    /**
     * 初始化SDK
     */
    private void stupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        NERtcEx.getInstance().setParameters(parameters); //先设置参数，后初始化

        NERtcOption options = new NERtcOption();

        try {
            NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.yun_xin_key), this, options);
        } catch (Exception e) {
            // 可能由于没有release导致初始化失败，release后再试一次
            NERtcEx.getInstance().release();
            try {
                NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.yun_xin_key), this, options);
            } catch (Exception ex) {
                Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        setLocalAudioEnable(true);
    }


    /**
     * 设置本地音频的可用性
     */
    private void setLocalAudioEnable(boolean enable) {
        NERtcEx.getInstance().enableLocalAudio(enable);
    }

    @Override
    protected ActivityCallingBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_calling);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public CallingViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(CallingViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().videoStatus.observe(this, integer -> {

        });
    }

    @Override
    public void onJoinChannel(int i, long l, long l1, long l2) {
        Log.e(TAG, "网易云信 onJoinChannel: " + i +","+ l + "," + l1 );
        //Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
        setLocalAudioEnable(true);
    }

    @Override
    public void onLeaveChannel(int i) {

        Log.e(TAG, "网易云信 onLeaveChannel: " + i );
    }

    @Override
    public void onUserJoined(long uid) {

//        Log.e(TAG, "网易云信 onUserJoined: " + uid );

    }

    @Override
    public void onUserJoined(long uid, NERtcUserJoinExtraInfo joinExtraInfo) {
        String customInfo = joinExtraInfo.customInfo;
        try {
            JSONObject jsonObject = new JSONObject(customInfo);
            Toast.makeText(this, jsonObject.getString("name")+"加入了房间", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "网易云信 onUserJoined: " + uid + joinExtraInfo.customInfo );

/*        //对方开启视频，按需设置画布及订阅视频
        NERtcEx.getInstance().setupRemoteVideoCanvas(binding.topVideo,uid);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,true);
        binding.topVideo.setMirror(true);*/

        obtainViewModel().videoChatList.add(new VideoChat(uid,"header","name",true,true));
        obtainViewModel().updateData();
    }

    @Override
    public void onUserLeave(long uid, int reason) {

        Log.e(TAG, "网易云信 onUserLeave: " + uid );
    }

    @Override
    public void onUserLeave(long uid, int reason, NERtcUserLeaveExtraInfo leaveExtraInfo) {

        Log.e(TAG, "网易云信 onUserLeave: " + uid );
    }

    @Override
    public void onUserAudioStart(long l) {

        Log.e(TAG, "网易云信 onUserAudioStart: " + l );
    }

    @Override
    public void onUserAudioStop(long l) {

        Log.e(TAG, "网易云信 onUserAudioStop: " + l );
    }

    @Override
    public void onUserVideoStart(long l, int i) {

        Log.e(TAG, "网易云信 onUserVideoStart: " + l +","+ i );
    }

    @Override
    public void onUserVideoStop(long l) {

        Log.e(TAG, "网易云信 onUserVideoStop: " + l );
    }

    @Override
    public void onDisconnect(int i) {

        Log.e(TAG, "网易云信 onDisconnect: " + i  );
    }

    @Override
    public void onClientRoleChange(int i, int i1) {

        Log.e(TAG, "网易云信 onClientRoleChange: " + i +","+ i1  );
    }

    @Override
    public void onItemClick(VideoChat videoChat) {

    }
}