package com.haohai.platform.fireforestplatform.ui.activity;

import static com.netease.lava.nertc.sdk.video.NERtcVideoStreamType.kNERtcVideoStreamTypeMain;
import static com.qweather.sdk.view.HeContext.context;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.event.YXCancelInvite;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingList;
import com.netease.lava.api.IVideoRender;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityCallingBinding;
import com.haohai.platform.fireforestplatform.event.Join;
import com.haohai.platform.fireforestplatform.event.YXClose;
import com.haohai.platform.fireforestplatform.event.YXControl;
import com.haohai.platform.fireforestplatform.event.YXReject;
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
import com.netease.nimlib.sdk.v2.V2NIMError;
import com.netease.nimlib.sdk.v2.V2NIMFailureCallback;
import com.netease.nimlib.sdk.v2.V2NIMSuccessCallback;
import com.netease.nimlib.sdk.v2.avsignalling.V2NIMSignallingService;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

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
        CommonData.calling = false;
        EventBus.getDefault().unregister(this);
        try {
            mediaPlayer.stop();
        }catch (Exception e){
            //
        }
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

    ///云信自定义控制指令
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(YXControl control) {
        HhLog.e("onGetMessage YXControl");
        long l = Long.parseLong((String) SPUtils.get(CallingActivity.this, SPValue.phone, ""));
        try {
            JSONObject jsonObject = new JSONObject(control.getInfo());
            String type = jsonObject.getString("type");
            //被请离房间
            if("pleaseLeave".equals(type)){
                finish();
            }
            //被禁言
            if("shutUp".equals(type)){
                obtainViewModel().hasAudio = false;
                obtainViewModel().closeMicrophone();
                binding.audioBack.setBackground(ContextCompat.getDrawable(this,R.drawable.white_circle));
                userAudioStop(l);
            }
            //被取消禁言
            if("unShutUp".equals(type)){
                obtainViewModel().hasAudio = true;
                obtainViewModel().openMicrophone();
                binding.audioBack.setBackground(ContextCompat.getDrawable(this,R.drawable.green_circle));
                userAudioStart(l);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ///云信有人拒绝
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(YXReject reject) {
        HhLog.e("onGetMessage YXReject");

        for (int i = 0; i < CommonData.invitedUserListForDelete.size(); i++) {
            CallingList calling = CommonData.invitedUserListForDelete.get(i);
            if(Objects.equals(calling.getPhone(), reject.uid)){
                CommonData.invitedUserListForDelete.remove(calling);
                if(CommonData.invitedUserListForDelete.isEmpty()){
                    finish();
                }
                return;
            }
        }
    }

    ///云信被取消邀请
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(YXCancelInvite cancelInvite) {
        HhLog.e("onGetMessage YXCancelInvite");

        if(!obtainViewModel().ing){
            finish();
        }
    }

    ///云信关闭
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(YXClose close) {
        HhLog.e("onGetMessage YXClose");

        finish();
    }

    private void changeVisible() {
        binding.flCalling.setVisibility(View.GONE);
        binding.llSpeaking.setVisibility(View.VISIBLE);

        parseStartDo();
    }

    private void parseStartDo() {
        if(obtainViewModel().ing){
            return;
        }
        obtainViewModel().ing = true;
        CommonData.calling = true;
        //以开启本地视频主流采集并发送为例
        NERtcEx.getInstance().enableLocalVideo(kNERtcVideoStreamTypeMain,true);

        if(obtainViewModel().isCalling){
            //主动呼叫-把自己视频放到主画面
            NERtcEx.getInstance().setupLocalVideoCanvas(binding.topVideo);
            NERtcEx.getInstance().startVideoPreview(kNERtcVideoStreamTypeMain);
            binding.topVideo.setMirror(true);
            binding.topVideo.setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_BALANCED);
            binding.leaderName.setText(CommonUtil.parseNull((String) SPUtils.get(this,SPValue.fullName,"")));
        }else{
            //被邀请人加入房间-先把把自己画面放到列表显示
            obtainViewModel().videoChatList.add(new VideoChat(Long.parseLong((String) SPUtils.get(this,SPValue.phone,"")),(String) SPUtils.get(this,SPValue.headUrl,""),(String) SPUtils.get(this,SPValue.fullName,""),(String) SPUtils.get(this,SPValue.roleName,""),true,true));
            obtainViewModel().updateData();
        }


        //关闭计时
        obtainViewModel().startTimer = false;
    }


    public MediaPlayer mediaPlayer;
    private void startRing() {
        mediaPlayer = MediaPlayer.create(CallingActivity.this, R.raw.wechat);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    private void startCallingVideo() {
        NERtcEx.getInstance().setupLocalVideoCanvas(binding.callingVideo);
        NERtcEx.getInstance().startVideoPreview(kNERtcVideoStreamTypeMain);
        binding.callingVideo.setMirror(true);
        binding.callingVideo.setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_BALANCED);
    }
    @SuppressLint("SetTextI18n")
    private void startTimer() {
        obtainViewModel().calendar.add(Calendar.SECOND,1);
        binding.callCount.setText(CommonUtil.parseZero(obtainViewModel().calendar.get(Calendar.HOUR_OF_DAY))+":"+
                CommonUtil.parseZero(obtainViewModel().calendar.get(Calendar.MINUTE))+":"+
                CommonUtil.parseZero(obtainViewModel().calendar.get(Calendar.SECOND)));
        if(obtainViewModel().calendar.get(Calendar.SECOND)>=30){
            if(obtainViewModel().isCalling){
                Toast.makeText(this, "对方未接听", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
        try{
            if(obtainViewModel().startTimer){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTimer();
                    }
                },1000);
            }else{
                mediaPlayer.stop();
            }
        }catch (Exception e){
            //
        }
    }


    private void joinChannelXD() {
        //Toast.makeText(this, "准备加入频道id " + CommonData.xdChannelId + "," + CommonData.testId, Toast.LENGTH_LONG).show();
        long selfUid = Long.parseLong((String) SPUtils.get(this, SPValue.phone, ""));
        NIMClient.getService(SignallingService.class).join(obtainViewModel().cId, selfUid, "", false).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {
                    @Override
                    public void onResult(int i, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        if (i == ResponseCode.RES_SUCCESS) {
                            //Toast.makeText(CallingActivity.this, "加入频道成功", Toast.LENGTH_SHORT).show();
                        } else if (i == ResponseCode.RES_CHANNEL_MEMBER_HAS_EXISTS) {
                            //Toast.makeText(CallingActivity.this, "已经在频道中", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(CallingActivity.this, "加入频道失败 code=" + i, Toast.LENGTH_SHORT).show();
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

        //开启计时
        obtainViewModel().startTimer = true;
        obtainViewModel().calendar = Calendar.getInstance();
        obtainViewModel().calendar.set(1,1,1,0,0,0);
        startTimer();
        startRing();
        startCallingVideo();

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
            obtainViewModel().accountId = (String) SPUtils.get(this,SPValue.phone,"");
            obtainViewModel().invite();
            binding.callingOpen.setVisibility(View.GONE);
            binding.callingCloseText.setVisibility(View.GONE);
            binding.callingFlSpace.setVisibility(View.GONE);
            String bName = "";
            if(CommonData.invitedUserList.size()==1){
                bName = CommonData.invitedUserList.get(0).getFullName();
            }else{
                bName = CommonData.invitedUserList.get(0).getFullName()+"等"+ CommonData.invitedUserList.size() +"人";
            }
            binding.callName.setText(bName);
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
                //取消邀请
                obtainViewModel().cancelInviteOther();
            }
        });
        CommonUtil.click(binding.audio, new Action() {
            @Override
            public void click() {
                obtainViewModel().hasAudio = !obtainViewModel().hasAudio;
                long l = Long.parseLong((String) SPUtils.get(CallingActivity.this, SPValue.phone, ""));
                if(obtainViewModel().hasAudio){
                    binding.audioBack.setBackground(ContextCompat.getDrawable(CallingActivity.this,R.drawable.green_circle));
                    binding.audioText.setText("关闭语音");
                    obtainViewModel().openMicrophone();
                    userAudioStart(l);
                }else{
                    binding.audioBack.setBackground(ContextCompat.getDrawable(CallingActivity.this,R.drawable.white_circle));
                    binding.audioText.setText("开启语音");
                    obtainViewModel().closeMicrophone();
                    userAudioStop(l);
                }
            }
        });
        CommonUtil.click(binding.video, new Action() {
            @Override
            public void click() {
                obtainViewModel().hasVideo = !obtainViewModel().hasVideo;
                long my = Long.parseLong((String) SPUtils.get(CallingActivity.this, SPValue.phone, ""));
                if(obtainViewModel().hasVideo){
                    binding.videoBack.setBackground(ContextCompat.getDrawable(CallingActivity.this,R.drawable.green_circle));
                    binding.videoText.setText("关闭视频");
                    NERtcEx.getInstance().enableLocalVideo(true);
                    //NERtcEx.getInstance().muteLocalVideoStream(false);
                    if((my + "").equals(obtainViewModel().accountId)){
                        binding.topVideo.setVisibility(View.VISIBLE);
                    }else{
                        videoStart(my);
                    }
                }else{
                    binding.videoBack.setBackground(ContextCompat.getDrawable(CallingActivity.this,R.drawable.white_circle));
                    binding.videoText.setText("开启视频");
                    NERtcEx.getInstance().enableLocalVideo(false);
                    //NERtcEx.getInstance().muteLocalVideoStream(true);
                    if((my + "").equals(obtainViewModel().accountId)){
                        binding.topVideo.setVisibility(View.GONE);
                    }else{
                        videoStop(my);
                    }
                }
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
                //Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

//        setLocalAudioEnable(true);
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


    @SuppressLint("SetTextI18n")
    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().videoStatus.observe(this, integer -> {
            binding.personNumber.setText(obtainViewModel().videoCounts+"");
        });
    }

    @Override
    public void onJoinChannel(int i, long l, long l1, long l2) {
        Log.e(TAG, "网易云信 onJoinChannel: " + i +","+ l + "," + l1 );
        //Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
//        setLocalAudioEnable(true);
    }

    @Override
    public void onLeaveChannel(int i) {

        Log.e(TAG, "网易云信 onLeaveChannel: " + i );
    }

    @Override
    public void onUserJoined(long uid) {

    }

    @Override
    public void onUserJoined(long uid, NERtcUserJoinExtraInfo joinExtraInfo) {

        Log.e(TAG, "网易云信 onUserJoined: " + uid + joinExtraInfo.customInfo );
        obtainViewModel().videoCounts++;
        obtainViewModel().videoStatus.postValue(obtainViewModel().videoCounts);
        obtainViewModel().openMicrophone();
        String customInfo = joinExtraInfo.customInfo;
        String name = "";
        String header = "";
        String role = "";
        try {
            JSONObject jsonObject = new JSONObject(customInfo);
            //Toast.makeText(this, jsonObject.getString("name")+"加入了房间", Toast.LENGTH_SHORT).show();
            role = jsonObject.getString("role");
            header = jsonObject.getString("header");
            name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

/*        //对方开启视频，按需设置画布及订阅视频
        NERtcEx.getInstance().setupRemoteVideoCanvas(binding.topVideo,uid);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,true);
        binding.topVideo.setMirror(true);
        binding.video.setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_BALANCED);*/

        if(Objects.equals(obtainViewModel().accountId, uid + "")){
            //发起人加入房间-主画面显示
            NERtcEx.getInstance().setupRemoteVideoCanvas(binding.topVideo,uid);
            NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,true);
            binding.topVideo.setMirror(true);
            binding.topVideo.setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_BALANCED);
            binding.leaderName.setText(name);
        }else{
            //被邀请人加入房间-列表画面显示
            obtainViewModel().videoChatList.add(new VideoChat(uid,header,name,role,true,true));
            obtainViewModel().updateData();
        }
    }

    @Override
    public void onUserLeave(long uid, int reason) {

    }

    @Override
    public void onUserLeave(long uid, int reason, NERtcUserLeaveExtraInfo leaveExtraInfo) {

        Log.e(TAG, "网易云信 onUserLeave: " + uid );
        if((uid + "").equals(obtainViewModel().accountId)){
            Toast.makeText(this, "视频通话已结束", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (int i = 0; i < obtainViewModel().videoChatList.size(); i++) {
            VideoChat videoChat = obtainViewModel().videoChatList.get(i);
            if(videoChat.getId() == uid){
                obtainViewModel().videoChatList.remove(videoChat);
                obtainViewModel().updateData();
                obtainViewModel().videoCounts--;
                HhLog.e("onUserLeave -" + obtainViewModel().videoCounts);
                if(obtainViewModel().videoCounts==1){
                    finish();
                }else{
                    obtainViewModel().videoStatus.postValue(obtainViewModel().videoCounts);
                }
                return;
            }
        }

    }

    @Override
    public void onUserAudioStart(long l) {

        Log.e(TAG, "网易云信 onUserAudioStart: " + l );
        userAudioStart(l);
    }
    public void userAudioStart(long l){
        for (int i = 0; i < obtainViewModel().videoChatList.size(); i++) {
            VideoChat model = obtainViewModel().videoChatList.get(i);
            if(l == model.getId()){
                model.setAudio(true);
                obtainViewModel().updateData();
                return;
            }
        }
    }

    @Override
    public void onUserAudioStop(long l) {

        Log.e(TAG, "网易云信 onUserAudioStop: " + l );
        userAudioStop(l);
    }
    public void userAudioStop(long l){
        for (int i = 0; i < obtainViewModel().videoChatList.size(); i++) {
            VideoChat model = obtainViewModel().videoChatList.get(i);
            if(l == model.getId()){
                model.setAudio(false);
                obtainViewModel().updateData();
                return;
            }
        }
    }

    @Override
    public void onUserVideoStart(long l, int i) {

        Log.e(TAG, "网易云信 onUserVideoStart: " + l +","+ i );
        videoStart(l);
    }

    private void videoStart(long l) {
        for (int j = 0; j < obtainViewModel().videoChatList.size(); j++) {
            VideoChat chat = obtainViewModel().videoChatList.get(j);
            if(l == chat.getId()){
                chat.setVideo(true);
                obtainViewModel().updateData();
                return;
            }
        }
    }

    @Override
    public void onUserVideoStop(long l) {

        Log.e(TAG, "网易云信 onUserVideoStop: " + l );
        videoStop(l);
    }

    private void videoStop(long l) {
        for (int j = 0; j < obtainViewModel().videoChatList.size(); j++) {
            VideoChat chat = obtainViewModel().videoChatList.get(j);
            if(l == chat.getId()){
                chat.setVideo(false);
                obtainViewModel().updateData();
                return;
            }
        }
    }

    @Override
    public void onDisconnect(int i) {

        Log.e(TAG, "网易云信 onDisconnect: " + i  );
    }

    @Override
    public void onClientRoleChange(int i, int i1) {

        Log.e(TAG, "网易云信 onClientRoleChange: " + i +","+ i1  );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(VideoChat videoChat) {
        long l = Long.parseLong((String) SPUtils.get(CallingActivity.this, SPValue.phone, ""));
        BottomSheetDialog dialog = new BottomSheetDialog(this,R.style.DialogNoBackground);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);
        ImageView close = view.findViewById(R.id.close);
        TextView name = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);
        TextView role = view.findViewById(R.id.role);
        TextView force = view.findViewById(R.id.force);
        TextView leave = view.findViewById(R.id.leave);
        name.setText(CommonUtil.parseNull(videoChat.getName()));
        phone.setText(CommonUtil.parseNull(videoChat.getId()+""));
        role.setText(CommonUtil.parseNull(videoChat.getRole()));
        if(videoChat.isAudio()){
            force.setText("禁言");
        }else{
            force.setText("解除禁言");
        }
        if(l==videoChat.getId()){
            leave.setText("离开");
        }else{
            leave.setText("请离");
        }
        CommonUtil.click(force, new Action() {
            @Override
            public void click() {
                //非发起人无操作权限
                if ((!obtainViewModel().isCalling) && (!(l + "").equals(obtainViewModel().accountId)) && (l != videoChat.getId())) {
                    Toast.makeText(CallingActivity.this, "只有发起人有操作权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                videoChat.setAudio(!videoChat.isAudio());
                JSONObject jsonObject = new JSONObject();
                try {
                    if (videoChat.isAudio()) {
                        //是本人
                        if (l == videoChat.getId()) {
                            binding.audioBack.setBackground(ContextCompat.getDrawable(CallingActivity.this, R.drawable.green_circle));
                            binding.audioText.setText("关闭语音");
                            obtainViewModel().openMicrophone();
                            userAudioStart(l);
                        } else {
                            //不是本人
                            jsonObject.put("type", "unShutUp");
                            NIMClient.getService(SignallingService.class).sendControl(obtainViewModel().cId, videoChat.getId() + "", jsonObject.toString());
                        }
                        force.setText("禁言");
                    } else {
                        //是本人
                        if (l == videoChat.getId()) {
                            binding.audioBack.setBackground(ContextCompat.getDrawable(CallingActivity.this, R.drawable.white_circle));
                            binding.audioText.setText("开启语音");
                            obtainViewModel().closeMicrophone();
                            userAudioStop(l);
                        } else {
                            //不是本人
                            jsonObject.put("type", "shutUp");
                            NIMClient.getService(SignallingService.class).sendControl(obtainViewModel().cId, videoChat.getId() + "", jsonObject.toString());
                        }
                        force.setText("解除禁言");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        CommonUtil.click(leave, new Action() {
            @Override
            public void click() {
                //非发起人无操作权限&&非本人
                if((!obtainViewModel().isCalling) && (!(l + "").equals(obtainViewModel().accountId)) && (l!=videoChat.getId())){
                    Toast.makeText(CallingActivity.this, "只有发起人有操作权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                //是本人
                if(l==videoChat.getId()){
                    finish();
                }else{
                    //不是本人
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type","pleaseLeave");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NIMClient.getService(SignallingService.class).sendControl(obtainViewModel().cId, videoChat.getId()+"", jsonObject.toString());
                }
            }
        });
        CommonUtil.click(close, new Action() {
            @Override
            public void click() {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}