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
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFireMessage;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SatelliteFireMessageInfoViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
    public final MutableLiveData<SatelliteFireMessage> messageBean = new MutableLiveData<>();


    public void barLeftClick(View v){
        ((SatelliteFireMessageInfoActivity)context).finish();
    }

    public void postData(String ids){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_MESSAGE_SATELLITE_INFO)
                .addParams("id",ids)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("postData " + ids);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<SatelliteFireMessage> list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<SatelliteFireMessage>>() {
                            }.getType());
                            if(!list.isEmpty()){
                                SatelliteFireMessage satelliteFireMessage = list.get(0);
                                messageBean.postValue(satelliteFireMessage);

                                //更新未读状态
                                updateReadState(ids);
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

    private void updateReadState(String ids) {
        HhHttp.method("PUT",URLConstant.PUT_MESSAGE_SATELLITE_STATE, new Gson().toJson(new CommonParams(1, ids)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = Objects.requireNonNull(response.body()).string();
                if(body.contains(":200,")){
                    EventBus.getDefault().post(new MessageRefresh());
                }
            }
        });
    }
}
