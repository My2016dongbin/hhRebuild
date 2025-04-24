package com.haohai.platform.fireforestplatform.ui.viewmodel;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.CallingListActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingList;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class CallingListViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
    public MultiTypeAdapter adapter;
    public List<CallingList> callingLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();


    public void barLeftClick(View v){
        ((CallingListActivity)context).finish();
    }

    public void getTrees(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.postString()
                .url(URLConstant.POST_PERSON_LIST)
                .content(new JSONObject().toString())
                .build()
                .connTimeOut(20000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("","POST_PERSON_LIST response " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            callingLists = new Gson().fromJson(String.valueOf(data),
                                    new TypeToken<List<CallingList>>(){}.getType());
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
        items.clear();
        if (callingLists != null && callingLists.size()!=0) {
            items.addAll(callingLists);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
