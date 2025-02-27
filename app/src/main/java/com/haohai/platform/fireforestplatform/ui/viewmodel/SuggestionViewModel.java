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
import com.haohai.platform.fireforestplatform.ui.activity.SuggestionActivity;
import com.haohai.platform.fireforestplatform.ui.bean.SuggestionDto;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.Suggestion;
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

public class SuggestionViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<News> newsList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 20;
    public final MutableLiveData<Integer> loadMore = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((SuggestionActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String toJson = new Gson().toJson(new SuggestionDto(page, limit, new SuggestionDto.Dto((String) SPUtils.get(context, SPValue.id, "1"))));
        HhLog.e("POST_SUGGESTION_LIST toJson " + toJson);
        HhHttp.postString()
                .url(URLConstant.POST_SUGGESTION_LIST)
                .content(toJson)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_SUGGESTION_LIST " + page + ", "  + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray dataList = data.getJSONArray("dataList");
                            newsList = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<Suggestion>>() {
                            }.getType());
                            if(page == 0){
                                loadMore.postValue(1);
                            }
                            if(newsList.size() < limit){
                                loadMore.postValue(0);
                            }
                            updateData();

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


    public void updateData() {
        if(page==1){
            items.clear();
        }
        if (newsList != null && newsList.size()!=0) {
            items.addAll(newsList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
