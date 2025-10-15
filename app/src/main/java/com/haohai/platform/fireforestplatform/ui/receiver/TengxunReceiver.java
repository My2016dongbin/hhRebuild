package com.haohai.platform.fireforestplatform.ui.receiver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.DoUpdate;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.old.BackgroundMp3Service;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.MainNews;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopNotification;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
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
            try {
                JSONObject jsonObject = new JSONObject(message.getCustomContent());
                id = jsonObject.getString("id");
                type = jsonObject.getString("type");
                content = jsonObject.getString("content");
                time = jsonObject.getString("time");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(Objects.equals(type, "12")){
                EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context, SPValue.mapIndex,3),"oneBody"));
            }else if(Objects.equals(type, "4")){
                Intent intent = new Intent(HhApplication.getInstance(), NewsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HhApplication.getInstance().startActivity(intent);
            }else{
                EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context,SPValue.messageIndex,2)));
            }
            EventBus.getDefault().post(new MessageRefresh());


        }
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
        try {
            JSONObject jsonObject = new JSONObject(xgPushShowedResult.getCustomContent());
            id = jsonObject.getString("id");
            type = jsonObject.getString("type");
            content = jsonObject.getString("content");
            time = jsonObject.getString("time");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Objects.equals(type, "4")){
            EventBus.getDefault().post(new MainNews());
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

        if("12".equals(finalType)){
            HhHttp.get()
                    .url(URLConstant.GET_MAP_ONE_BODY_DETAIL)
                    .addParams("id", id)
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(null, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            HhLog.e("OneBodyInfo - postData " + id);
                            HhLog.e("OneBodyInfo - postData " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String code = jsonObject.getString("code");
                                if (Objects.equals(code, "200")) {
                                    JSONObject data = (JSONObject) jsonObject.getJSONArray("data").get(0);
                                    OneBodyFire oneBodyFire = new Gson().fromJson(String.valueOf(data), OneBodyFire.class);
                                    PopNotification.build()
                                            .setIconResId(R.drawable.ic_icon)
                                            .setMessage(xgPushShowedResult.getContent())
                                            .setMargin(20,20,20,0)
                                            .setRadius(40)
                                            .setTitle(xgPushShowedResult.getTitle())
                                            .autoDismiss(60000)
                                            .setOnPopNotificationClickListener((dialog, v) -> {
                                                new InputDialog(parseMessageType(finalType) + "通知",
                                                        "报警内容："+oneBodyFire.getAddress()+oneBodyFire.getAlarmType()
                                                                + "\n报警时间："
                                                                + CommonUtil.parse19String(oneBodyFire.getAlarmDatetime(),"")
                                                                + "\n经纬度："
                                                                + oneBodyFire.getAlarmLongitude() + "," + oneBodyFire.getAlarmLatitude(),
                                                        "反馈", "取消", context.getString(R.string.feedback))
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
                                } else {

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Exception e, int id) {

                        }
                    });
        } else{
            HhHttp.get()
                    .url(URLConstant.GET_TASK_INFO)
                    .addParams("id", id)
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(null, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            HhLog.e("TaskListInfo - postData " + id);
                            HhLog.e("TaskListInfo - postData " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String code = jsonObject.getString("code");
                                if (Objects.equals(code, "200")) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    TaskList taskList = new Gson().fromJson(String.valueOf(data), TaskList.class);
                                    PopNotification.build()
                                            .setIconResId(R.drawable.ic_icon)
                                            .setMessage(xgPushShowedResult.getContent())
                                            .setMargin(20,20,20,0)
                                            .setRadius(40)
                                            .setTitle(xgPushShowedResult.getTitle())
                                            .autoDismiss(60000)
                                            .setOnPopNotificationClickListener((dialog, v) -> {
                                                new InputDialog(parseMessageType(finalType) + "通知",
                                                        "任务内容:" + taskList.getTaskContent()
                                                                + "\n任务截止时间："
                                                                + taskList.getTaskEndTime()
                                                                + "\n经纬度："
                                                                + taskList.getPosition().getLng() + "," + taskList.getPosition().getLat(),
                                                        "反馈", "取消", context.getString(R.string.feedback))
                                                        /*.setCustomView(new OnBindView(R.layout.dialog_header_layout) {
                                                            @Override
                                                            public void onBind(Object dialog, View v) {
                                                                TextView task_time = v.findViewById(R.id.task_time);
                                                                TextView task_lat_lng = v.findViewById(R.id.task_lat_lng);
                                                                task_time.setText("任务截止时间：2025-10-31 23:59:59");
                                                                task_lat_lng.setText("经纬度：132.324231,32.432533");
                                                            }
                                                        })*/
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
                                } else {

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Exception e, int id) {

                        }
                    });
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
