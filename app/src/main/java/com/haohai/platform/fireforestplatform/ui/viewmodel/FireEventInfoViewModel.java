package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.FireEventInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.FireTimeLine;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FireEventInfoViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<List<FireTimeLine>> timeLineList = new MutableLiveData<>();

    public void start(Context context){
        this.context = context;
    }

    public void barLeftClick(View v){
        ((FireEventInfoActivity)context).finish();
    }

    public void getTimeLine(String fireId){
        if(fireId == null || fireId.length() == 0){
            timeLineList.setValue(new ArrayList<>());
            return;
        }
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_FIRE_TIME_WIRE + fireId)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_FIRE_TIME_WIRE " + fireId + "," + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.optJSONArray("data");
                            List<FireTimeLine> list;
                            if(data != null){
                                list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<FireTimeLine>>() {
                                }.getType());
                            }else{
                                list = new ArrayList<>();
                            }
                            timeLineList.postValue(list);
                        } catch (JSONException e) {
                            HhLog.e("GET_FIRE_TIME_WIRE error " + e.getMessage());
                            timeLineList.postValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        timeLineList.postValue(new ArrayList<>());
                    }
                });
    }
}
