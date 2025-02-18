package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class ModelViewModel extends BaseViewModel {
    public Context context;
    public List<News> newsList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((NewsActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.postString()
                .url(URLConstant.POST_NEWS)
                .content(new Gson().toJson(new News((String) SPUtils.get(context, SPValue.groupId,""))))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            newsList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<News>>() {
                            }.getType());


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
}
