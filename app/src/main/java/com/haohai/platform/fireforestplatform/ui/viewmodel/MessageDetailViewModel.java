package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.MessageDetailActivity;
import com.haohai.platform.fireforestplatform.ui.bean.ReadParam;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import okhttp3.Call;

public class MessageDetailViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public Message message = new Message();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((MessageDetailActivity)context).finish();
    }

    public void postRead(){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        String read = new Gson().toJson(new ReadParam(message.getId(), "read"));
        HhHttp.postString()
                .url(URLConstant.GET_CHANGE_STATE_NEW)
                .content(read)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.postValue(new LoadingEvent(false));
                        HhLog.e("GET_CHANGE_STATE_NEW " + URLConstant.GET_CHANGE_STATE_NEW);
                        HhLog.e("GET_CHANGE_STATE_NEW " + read);
                        HhLog.e("GET_CHANGE_STATE_NEW " + response);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.postValue(new LoadingEvent(false));
                    }
                });
    }
}
