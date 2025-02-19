package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MonitorFireMessageInfoViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
    public final MutableLiveData<MonitorFireMessage> messageBean = new MutableLiveData<>();


    public void barLeftClick(View v){
        ((MonitorFireMessageInfoActivity)context).finish();
    }

    public void postData(String ids){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_MESSAGE_MONITOR_INFO)
                .addParams("id",ids)
                .addParams("type","appInternet")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("postData " + ids);
                        HhLog.e("GET_MESSAGE_MONITOR_INFO " + response);
                        HhLog.e(response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<MonitorFireMessage> list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<MonitorFireMessage>>() {
                            }.getType());
                            //更新未读状态
                            updateReadState(ids,list);
                            if(!list.isEmpty()){
                                MonitorFireMessage monitorFireMessage = list.get(0);
                                messageBean.postValue(monitorFireMessage);
                            }else{
                                Toast.makeText(context, "该消息已删除", Toast.LENGTH_SHORT).show();
                                ((MonitorFireMessageInfoActivity)context).finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }
    public void postDataDetail(String ids){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_MESSAGE_MONITOR_INFO_DETAIL)
                .addParams("id",ids)
                .addParams("type","appInternet")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("postData " + ids);
                        HhLog.e("GET_MESSAGE_MONITOR_INFO " + response);
                        HhLog.e(response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<MonitorFireMessage> list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<MonitorFireMessage>>() {
                            }.getType());
                            //更新未读状态
                            updateReadState(ids,list);
                            if(!list.isEmpty()){
                                MonitorFireMessage monitorFireMessage = list.get(0);
                                messageBean.postValue(monitorFireMessage);
                            }else{
                                Toast.makeText(context, "该消息已删除", Toast.LENGTH_SHORT).show();
                                ((MonitorFireMessageInfoActivity)context).finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    private void updateReadState(String ids, List<MonitorFireMessage> list) {
        MonitorFireMessage model = new MonitorFireMessage();
        if(list!=null && list.size()>0){
            model = list.get(0);
        }
        RequestParams params = new RequestParams(URLConstant.GET_CHANGE_STATE_NEW);
        //params.addParameter("messageId",model.getAppMessageId());
        //params.addParameter("foreignId",ids);
        //params.addParameter("messageType","monitorFirealarm");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //params.setBodyContent(jsonObject.toString());
        params.addParameter("messageId",ids);
        MonitorFireMessage finalModel = model;
        HhHttp.methodX(HttpMethod.GET, params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("onResponse: params " + params );
                HhLog.e("onResponse: " + result );
                HhLog.e( "onResponse messageId: " + finalModel.getAppMessageId()  );
                HhLog.e( "onResponse foreignId: " + ids  );
                if(result.contains(":200,")){
                    EventBus.getDefault().post(new MessageRefresh());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
