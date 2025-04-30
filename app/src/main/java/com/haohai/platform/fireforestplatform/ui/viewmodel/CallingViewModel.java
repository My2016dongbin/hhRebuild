package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.baidu.mapframework.commonlib.asynchttp.RequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MessageChange;
import com.haohai.platform.fireforestplatform.ui.activity.CallingActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingList;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.VideoChat;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcJoinChannelOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelBaseInfo;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class CallingViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<VideoChat> videoChatList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public final MutableLiveData<Integer> videoStatus = new MutableLiveData<>(1);
    public int videoCounts = 1;
    public void start(Context context){
        this.context = context;
    }

    public AudioManager audioManager;

    public Calendar calendar = Calendar.getInstance();

    public boolean isCalling ;
    public boolean startTimer = false;
    public boolean hasAudio = true;
    public boolean hasVideo = true;
    public String channelId ;
    public String accountId ;
    public String requestId ;
    public String customInfo ;

    public String wyyToken ;

    public String roomName ;
    public String roomId ;
    public String cId ;
    public boolean ing = false ;//已接听


    public void barLeftClick(View v){
        ((CallingActivity)context).finish();
    }



    public void updateData() {
        if (videoChatList != null && videoChatList.size()!=0) {
            items.clear();
            items.addAll(videoChatList);

            assertAllRegistered(adapter, items);
            adapter.notifyDataSetChanged();
        }
    }

    public void updateDataAt(int i) {
        assertAllRegistered(adapter, items);
        adapter.notifyItemChanged(i,videoChatList.get(i));
    }

    public void updateDataAdd() {
        if (videoChatList != null && videoChatList.size()!=0) {
//            items.clear();
            items.add(videoChatList.get(videoChatList.size()-1));

            assertAllRegistered(adapter, items);
            adapter.notifyItemInserted(items.size()-1);
        }
    }

    public void updateDataDelete(int i) {
        items.remove(i);

        assertAllRegistered(adapter, items);
        adapter.notifyItemRemoved(i);
    }


    /**
     * 接受对方的的邀请并加入频道
     */
    public void acceptInvite() {
        InviteParamBuilder inviteParam = new InviteParamBuilder(channelId,accountId,requestId);
        inviteParam.customInfo("我要接受邀请");
        NIMClient.getService(SignallingService.class).acceptInviteAndJoin(inviteParam, Long.parseLong((String) SPUtils.get(context, SPValue.phone,""))).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {

                    @Override
                    public void onResult(int code, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        //参考官方文档中关于api以及错误码的说明
                        if (code == ResponseCode.RES_SUCCESS) {
                            //Toast.makeText(CallingActivity.this, "接收邀请成功"+CommonData.testId, Toast.LENGTH_SHORT).show();

                            //加入音频房间
                            joinRoom(requestId);

                        } else {
                            //Toast.makeText(CallingActivity.this, "接收邀请返回的结果 ， code = " + code +(throwable == null ? "" : ", throwable = " +throwable.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 拒绝对方的邀请
     */
    public void rejectInvite(String customInfo, InvitedEvent invitedEvent) {
        InviteParamBuilder inviteParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                invitedEvent.getFromAccountId(),
                invitedEvent.getRequestId());
        if (!TextUtils.isEmpty(customInfo)) {
            inviteParam.customInfo(customInfo);
        }
        NIMClient.getService(SignallingService.class).rejectInvite(inviteParam);
    }

    /**
     * 加入音频房间
     * @param roomName
     */
    public void joinRoom(String roomName) {
        HhHttp.get()
                .url(URLConstant.JOIN_ROOM)
                .addParams("uid",(String) SPUtils.get(context, SPValue.phone,""))
                .addParams("channelName",roomName)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("JOIN_ROOM" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject model = (JSONObject) data.get(0);
                            HhLog.e( "onSuccess: model = " + model );
                            String token = model.getString("token");
                            wyyToken = token;

                            //Toast.makeText(CallingActivity.this, "准备加入房间："+roomName, Toast.LENGTH_SHORT).show();
                            NERtcJoinChannelOptions channelOptions = new NERtcJoinChannelOptions();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name",SPUtils.get(context, SPValue.fullName,""));
                            jsonObject.put("header",SPUtils.get(context, SPValue.headUrl,""));
                            jsonObject.put("role",SPUtils.get(context, SPValue.roleName,""));
                            channelOptions.customInfo = jsonObject.toString();
                            NERtcEx.getInstance().joinChannel(token,roomName,Long.parseLong((String) SPUtils.get(context, SPValue.phone,"")), channelOptions);
                            HhLog.e( "onSuccess: yunxin " + token);
                            HhLog.e( "onSuccess: yunxin " + roomName );
                            HhLog.e( "onSuccess: yunxin " + Long.parseLong((String) SPUtils.get(context, SPValue.phone,"")) );

                            openMicrophone();
                            openSpeaker();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("JOIN_ROOM error" + e.toString());
                    }
                });
    }


    public void openSpeaker(){
        audioManager.setSpeakerphoneOn(true);
        audioManager.setSpeakerphoneOn(true);
    }
    public void closeSpeaker(){
        audioManager.setSpeakerphoneOn(false);
        audioManager.setSpeakerphoneOn(false);
    }
    public void openMicrophone(){
        setLocalAudioEnable(true);
    }
    public void closeMicrophone(){
        setLocalAudioEnable(false);
    }

    /**
     * 设置本地音频的可用性
     */
    public void setLocalAudioEnable(boolean enable) {
        NERtcEx.getInstance().enableLocalAudio(enable);
    }

    public void invite() {
        String dateStr = new Date().getTime()+"";
        String phone = (String) SPUtils.get(context, SPValue.phone, "");
        roomName = phone+dateStr.substring(dateStr.length()-4);
        CommonData.audioRoomName = roomName;
        HhHttp.post()
                .url(URLConstant.YX_CREATE)
                .addParams("uid", phone)
                .addParams("channelName",roomName)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "网易云信 onSuccess: YX_CREATE = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject model = (JSONObject) data.get(0);
                            roomId = model.getString("cid");
                            CommonData.audioRoomId = roomId;

                            //2.创建
                            NIMClient.getService(SignallingService.class).create(ChannelType.CUSTOM, roomName, "").setCallback(
                                    new RequestCallbackWrapper<ChannelBaseInfo>() {

                                        @Override
                                        public void onResult(int i, ChannelBaseInfo channelBaseInfo, Throwable throwable) {
                                            if (i == ResponseCode.RES_SUCCESS) {
                                                Log.e("TAG", "网易云信 RES_SUCCESS = ");
                                                cId = channelBaseInfo.getChannelId();
                                                CommonData.xdChannelId = cId;
                                                //Toast.makeText(IMActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                                //3.发起邀请
                                                inviteOther();
                                            } else {
                                                Log.e("TAG", "网易云信 ERROR = " + i);
                                                //Toast.makeText(IMActivity.this, "创建失败， code = " + i + (throwable == null ? "" : ", throwable = " + throwable.getMessage()), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "视频邀请失败", Toast.LENGTH_SHORT).show();
                            ((CallingActivity)context).finish();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        Toast.makeText(context, "视频邀请失败", Toast.LENGTH_SHORT).show();
                        ((CallingActivity)context).finish();
                    }
                });
    }

    int number = 0;
    private void inviteOther() {
        number = 0;
        CommonData.invitedReqList = new ArrayList<>();
        for (int i = 0; i < CommonData.invitedUserList.size(); i++) {
            CallingList callingModel = CommonData.invitedUserList.get(i);
            InviteParamBuilder param = new InviteParamBuilder(cId, callingModel.getPhone(), roomName);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",SPUtils.get(context, SPValue.fullName,""));
                jsonObject.put("header",SPUtils.get(context, SPValue.headUrl,""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            param.customInfo(jsonObject.toString());
            CommonData.invitedReqList.add(param);
            NIMClient.getService(SignallingService.class).invite(param).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    //Toast.makeText(context, "邀请成功 ：cId = " + cId + ", roomName = " + roomName, Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onFailed(int code) {
                    HhLog.e("网易云信inviteOther " + code);
                    for (int i = 0; i < CommonData.invitedUserListForDelete.size(); i++) {
                        CallingList calling = CommonData.invitedUserListForDelete.get(i);
                        if(Objects.equals(calling.getPhone(), callingModel.getPhone())){
                            if(code == 10202){
                                Toast.makeText(context, callingModel.getFullName()+"不在线", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, callingModel.getFullName()+"邀请失败", Toast.LENGTH_SHORT).show();
                            }
                            CommonData.invitedUserListForDelete.remove(calling);
                            if(CommonData.invitedUserListForDelete.isEmpty()){
                                ((CallingActivity)context).finish();
                                return;
                            }
                        }
                    }
                    number++;
                    if(number == CommonData.invitedReqList.size()){
                        ((CallingActivity)context).finish();
                    }
                }

                @Override
                public void onException(Throwable exception) {
                    Log.e("TAG", "网易云信 onException = ");
                    //Toast.makeText(IMActivity.this, "邀请异常 ：exception = " + exception, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * 取消邀请别人
     */
    public void cancelInviteOther() {
        if(CommonData.invitedReqList == null || CommonData.invitedReqList.isEmpty() ){
            ((CallingActivity)context).finish();
            return;
        }
        cancelOther();
        Toast.makeText(context, "已取消邀请", Toast.LENGTH_SHORT).show();
        ((CallingActivity)context).finish();
    }

    public void cancelOther(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < CommonData.invitedReqList.size(); i++) {
                    InviteParamBuilder inviteParamBuilder = CommonData.invitedReqList.get(i);
                    NIMClient.getService(SignallingService.class).cancelInvite(inviteParamBuilder).setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            //Toast.makeText(context, "已取消邀请", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(int code) {
                            //Toast.makeText(context, "取消邀请失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            Toast.makeText(context, "取消邀请异常", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(CallingActivity.this, "取消邀请异常 ：exception = " + exception, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                ((CallingActivity)context).finish();
            }
        }.start();
    }
}
