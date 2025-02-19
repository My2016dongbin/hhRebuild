package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.multitype.Alarm;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class AlarmViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Alarm> messageList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 10;
    public void start(Context context){
        this.context = context;
    }

    public void postData(boolean load){
        if(load){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        HhHttp.get()
                .url(URLConstant.GET_NOTICE_PAGE)
                .addParams("pageNum",page+"")
                .addParams("pageSize",limit+"")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_NOTICE_PAGE" + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("rows");
                            messageList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Alarm>>() {
                            }.getType());
                            updateData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateData();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        updateData();
                    }
                });


        /*//TODO 调试1.23
        messageList.add(new Alarm("这是一条测试预警监测事件"));
        messageList.add(new Alarm("预警监测事件预警监测事件预警监测事件预警监测事件预警监测事件预警监测事件"));
        updateData();*/
    }


    public void updateData() {
        if(page==1){
            items.clear();
        }
        if (messageList != null && messageList.size()!=0) {
            items.addAll(messageList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
