package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

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
import com.haohai.platform.fireforestplatform.ui.activity.FireEventListActivity;
import com.haohai.platform.fireforestplatform.ui.bean.FireEventParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.FireEvent;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class FireEventListViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<FireEvent> fireEventList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 20;
    public final MutableLiveData<Integer> loadMore = new MutableLiveData<>();

    public void start(Context context){
        this.context = context;
    }

    public void barLeftClick(View v){
        ((FireEventListActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        FireEventParams.Dto dto = new FireEventParams.Dto("", "", "", "", "", "");
        String content = new Gson().toJson(new FireEventParams(limit, page, dto));
        HhHttp.postString()
                .url(URLConstant.POST_FIRE_EVENT_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_FIRE_EVENT_LIST " + page + ", "  + content);
                        HhLog.e("POST_FIRE_EVENT_LIST " + page + ", "  + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                int totalSize = obj.optInt("totalSize");
                                JSONArray dataList = obj.optJSONArray("dataList");
                                if(dataList != null){
                                    fireEventList = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<FireEvent>>() {
                                    }.getType());
                                }else{
                                    fireEventList = new ArrayList<>();
                                }
                                updateData();
                                if(items.size() >= totalSize || fireEventList.size() < limit){
                                    loadMore.postValue(0);
                                }else{
                                    loadMore.postValue(1);
                                }
                            }else{
                                fireEventList.clear();
                                updateData();
                                loadMore.postValue(0);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            HhLog.e("POST_FIRE_EVENT_LIST error " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void updateData() {
        if(page==1){
            items.clear();
        }
        if (fireEventList != null && fireEventList.size()!=0) {
            items.addAll(fireEventList);
        }else if(page==1){
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
