package com.haohai.platform.fireforestplatform.ui.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.DoUpdate;
import com.haohai.platform.fireforestplatform.event.Ext;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.old.BackgroundMp3Service;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.activity.ResourceSearchActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopNotification;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.util.TextInfo;
import com.tencent.android.tpush.NotificationAction;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class TengxunReceiver extends XGPushBaseReceiver{
    private static final String TAG = TengxunReceiver.class.getSimpleName();

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.e(TAG, "onRegisterResult: ");
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.e(TAG, "onUnregisterResult: " );
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.e(TAG, "onSetTagResult: ");
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.e(TAG, "onDeleteTagResult: ");
    }

    @Override
    public void onSetAccountResult(Context context, int i, String s) {
        Log.e(TAG, "onSetAccountResult: ");
    }

    @Override
    public void onDeleteAccountResult(Context context, int i, String s) {
        Log.e(TAG, "onDeleteAccountResult: ");
    }

    /**
     * 消息透传
     * @param context
     * @param xgPushTextMessage
     */
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.e(TAG, "onTextMessage: ");
        String title = xgPushTextMessage.getTitle();
        if(title!=null && title.contains("update")){
            EventBus.getDefault().post(new DoUpdate());
        }
    }

    /**
     * 消息点击回调
     * @param context
     */
    @Override
    public void onNotificationClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        Log.e(TAG, "onNotificationClickedResult: ");
        if (message.getActionType() == NotificationAction.clicked.getType()) {// 通知在通知栏被点击   APP自己处理点击的相关动作
            Log.e(TAG, "onNotificationClickedResult: 通知被点击了" +message.getActivityName());
            Log.e(TAG, "onNotificationClickedResult: 通知被点击了" +message.getCustomContent());
            String id = "";
            String type = "";
            String content = "";
            String time = "";
            String messageType = "";
            try {
                JSONObject jsonObject = new JSONObject(message.getCustomContent());
                messageType = jsonObject.getString("messageType");//cyLogout登出推送 navigationSyncApp同步导航推送
                if("cyLogout".equals(messageType)){
                    //outLogin_();
                    return;
                }
                if("navigationSyncApp".equals(messageType)){
                /* 同步导航
                {
                    "gaode": {
                        "origin": "120.541377,36.11884",
                        "destination": "120.71525,36.409518",
                        "output": "JSON",
                        "key": "60f04b98804a8a1d24cc2953b5dba495",
                        "type": "walking",//walking步行 driving驾车
                        "strategy": 0//导航策略ID
                    },
                    "messageType": "navigationSyncApp"
                }
                * */
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            guide_(jsonObject);
                        }
                    },3000);
                    return;
                }
                if("longMessage".equals(messageType)){
                    longMessage();
                    return;
                }
                id = jsonObject.getString("id");
                type = jsonObject.getString("type");
                content = jsonObject.getString("content");
                time = jsonObject.getString("time");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(Objects.equals(type, "12")){
                if(CommonData.hasMainMap)EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context, SPValue.mapIndex,3),"oneBody"));
            }else{
                if(CommonData.hasMainMessage)EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context,SPValue.messageIndex,2)));
            }
            EventBus.getDefault().post(new MessageRefresh());


        }
    }

    private void longMessage() {
        //MessageDialog.show("","");
    }

    /**
     * 通知栏接受报警
     * @param context
     * @param xgPushShowedResult
     */
    @Override
    public void onNotificationShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.e(TAG, "onNotificationShowedResult: " + xgPushShowedResult.getActivity());
        Log.e(TAG, "onNotificationShowedResult: " + xgPushShowedResult.getContent());
        Log.e(TAG, "onNotificationShowedResult: " + xgPushShowedResult.getTitle());
        Log.e(TAG, "onNotificationShowedResult: " + xgPushShowedResult.getMsgId());
        Log.e(TAG, "onNotificationShowedResult: " + xgPushShowedResult.getNotifactionId());
        Log.e(TAG, "onNotificationShowedResult: customContent" + xgPushShowedResult.getCustomContent());
        String id = "";
        String type = "";
        String content = "";
        String time = "";
        String messageType = "";
        try {
            JSONObject jsonObject = new JSONObject(xgPushShowedResult.getCustomContent());
            HhLog.e("推送参数 。" + jsonObject.toString());
            messageType = jsonObject.getString("messageType");//cyLogout登出推送 navigationSyncApp同步导航推送
            if("cyLogout".equals(messageType)){
                outLogin_();
                return;
            }
            if("navigationSyncApp".equals(messageType)){
                /* 同步导航
                {
                    "gaode": {
                        "origin": "120.541377,36.11884",
                        "destination": "120.71525,36.409518",
                        "output": "JSON",
                        "key": "60f04b98804a8a1d24cc2953b5dba495",
                        "type": "walking",//walking步行 driving驾车
                        "strategy": 0//导航策略ID
                    },
                    "messageType": "navigationSyncApp"
                }
                * */
                guide_(jsonObject);
                return;
            }
            if("longMessage".equals(messageType)){
                longMessage();
                return;
            }
            id = jsonObject.getString("id");
            type = jsonObject.getString("type");
            content = jsonObject.getString("content");
            time = jsonObject.getString("time");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(context, BackgroundMp3Service.class);
        CommonData.warnType = type;
        context.startService(intent);

        EventBus.getDefault().post(new MessageRefresh());
        if(CommonData.isUpdate){
            return;
        }
        String finalType = type;
        String finalContent = content;
        String finalId = id;
        PopNotification.build()
                .setIconResId(R.drawable.ic_icon)
                .setMessage(xgPushShowedResult.getContent())
                .setMargin(20,20,20,0)
                .setRadius(40)
                .setTitle(xgPushShowedResult.getTitle())
                .autoDismiss(60000)
                .setOnPopNotificationClickListener((dialog, v) -> {
                    new InputDialog(parseMessageType(finalType) + "通知", finalContent, "反馈", "取消", context.getString(R.string.feedback))
                            .setCancelable(false)
                            .setOkButton((baseDialog, v2, inputStr) -> {
                                if(inputStr.isEmpty() /*|| Objects.equals(inputStr, context.getString(R.string.feedback))*/) {
                                    Toast.makeText(context, "请输入反馈信息", Toast.LENGTH_SHORT).show();
                                    return true;
                                }else{
                                    postFeedBack(finalId, inputStr);
                                }
                                return false;
                            })
                            .show();
                    return false;
                })
                .show();
    }

    private void outLogin_() {
        HhApplication context = HhApplication.getInstance();
        MessageDialog.show("温馨提示", "登录信息失效，请重新登录！","确定","取消")
                .setButtonOrientation(LinearLayout.HORIZONTAL)
                .setOkTextInfo(new TextInfo().setFontColor(context.getResources().getColor(R.color.text_color_red)))
                .setOkButtonClickListener((dialog, v1) -> {
                    out_();
                    return false;
                })
                .setCancelButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        out_();
                        return false;
                    }
                }).setCancelable(false).setMaskColor(HhApplication.getInstance().getResources().getColor(R.color.trans_mask));
    }

    private void out_() {
        boolean login = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
        if(!login){
            return;
        }
        
        HhApplication context = HhApplication.getInstance();
        SPUtils.put(context,SPValue.login,false);
        CommonData.token = "";
        CommonData.videoAddingIndex = 0;
        CommonData.videoDeleteIndex = 0;
        CommonData.videoDeleteMonitorId = "";
        CommonData.videoDeleteChannelId = "";
        CommonData.videoPlayingIndexList = new ArrayList<>();
        CommonData.videoDeleteModelList = new ArrayList<>();
        CommonData.mainTabIndex = 0;
        CommonData.walkDistance = 0;
        SPUtils.put(context,SPValue.token,"");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        EventBus.getDefault().post(new Ext());
    }

    private void guide_(JSONObject jsonObject) {
        boolean login = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
        if(!login){
            return;
        }
        try {
            JSONObject gaode = jsonObject.getJSONObject("gaode");
            String destination = gaode.getString("destination");
            double longitude = 0;
            double latitude = 0;
            if(destination.contains(",")){
                String[] strings = destination.split(",");
                longitude = Double.parseDouble(strings[0]);
                latitude = Double.parseDouble(strings[1]);
            }
            String type = gaode.getString("type");//walking步行 driving驾车
            int strategy = 0;
            if(!Objects.equals(type, "walking")){
                strategy = gaode.getInt("strategy");//步行无此字段
            }
            HhApplication context = HhApplication.getInstance();

            double finalLatitude = latitude;
            double finalLongitude = longitude;
            int finalStrategy = strategy;
            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_guide) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    TextView text_close;
                    TextView text_confirm;
                    text_close = v.findViewById(R.id.text_close);
                    text_confirm = v.findViewById(R.id.text_confirm);
                    text_close.setOnClickListener(v13 -> {
                        dialog.dismiss();
                    });
                    text_confirm.setOnClickListener(v14 -> {
                        NaviSetting.updatePrivacyShow(context, true, true);
                        NaviSetting.updatePrivacyAgree(context, true);

                        //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
                        //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                        //启动导航组件
                        //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
                        double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
                        LatLng latLng_start = new LatLng(doubles_start[0],doubles_start[1]);
                        double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(finalLatitude, finalLongitude);
                        LatLng latLng_end = new LatLng(doubles_end[0],doubles_end[1]);


                        Poi start = new Poi("", latLng_start, "");
                        Poi end = new Poi("目的地", latLng_end, "");
                        AmapNaviParams params = new AmapNaviParams(start, null, end, (Objects.equals(type, "walking"))?AmapNaviType.WALK:AmapNaviType.DRIVER, AmapPageType.NAVI);
                        if(!Objects.equals(type, "walking")){
                            params.setRouteStrategy(finalStrategy);
                        }
                        params.setUseInnerVoice(true);
                        AmapNaviPage.getInstance().showRouteActivity(context, params, new INaviInfoCallback() {
                            @Override
                            public void onInitNaviFailure() {

                            }

                            @Override
                            public void onGetNavigationText(String s) {

                            }

                            @Override
                            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                            }

                            @Override
                            public void onArriveDestination(boolean b) {

                            }

                            @Override
                            public void onStartNavi(int i) {

                            }

                            @Override
                            public void onCalculateRouteSuccess(int[] ints) {

                            }

                            @Override
                            public void onCalculateRouteFailure(int i) {

                            }

                            @Override
                            public void onStopSpeaking() {

                            }

                            @Override
                            public void onReCalculateRoute(int i) {

                            }

                            @Override
                            public void onExitPage(int i) {

                            }

                            @Override
                            public void onStrategyChanged(int i) {

                            }

                            @Override
                            public void onArrivedWayPoint(int i) {

                            }

                            @Override
                            public void onMapTypeChanged(int i) {

                            }

                            @Override
                            public void onNaviDirectionChanged(int i) {

                            }

                            @Override
                            public void onDayAndNightModeChanged(int i) {

                            }

                            @Override
                            public void onBroadcastModeChanged(int i) {

                            }

                            @Override
                            public void onScaleAutoChanged(boolean b) {

                            }

                            @Override
                            public View getCustomMiddleView() {
                                return null;
                            }

                            @Override
                            public View getCustomNaviView() {
                                return null;
                            }

                            @Override
                            public View getCustomNaviBottomView() {
                                return null;
                            }
                        });

                        dialog.dismiss();
                    });
                }
            }).setCancelable(false).setMaskColor(HhApplication.getInstance().getResources().getColor(R.color.trans_mask));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseMessageType(String finalType) {
        String type = "";
        if(Objects.equals(finalType, "12")){
            type = "报警";
        }else{
            type = "任务";
        }

        return type;
    }

    private void postFeedBack(String id, String feedBack) {
        HhHttp.postString().content(new Gson().toJson(new CommonParams(id,feedBack,"","")))
                .url(URLConstant.POST_PUSH_FEEDBACK)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {
                Log.e(TAG, "parseNetworkResponse: postFeedBack " );
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: postFeedBack " + e.toString() );
            }

            @Override
            public void onResponse(Object response, int id) {
                Log.e(TAG, "onResponse: postFeedBack " + response );
            }
        });
    }
}
