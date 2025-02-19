package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.ComprehensiveList;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
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

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class ComprehensiveListViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public List<ComprehensiveList> taskLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ComprehensiveListActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto=new JSONObject();
            dto.put("type",3);
            jsonObject.put("dto",dto);
            jsonObject.put("limit",200);
            jsonObject.put("page",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.POST_COMPREHENSIVE_LIST)
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_COMPREHENSIVE_LIST " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            JSONObject obj = (JSONObject) data.get(0);
                            JSONArray dataList = obj.getJSONArray("dataList");
                            taskLists = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<ComprehensiveList>>() {
                            }.getType());
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

//        List<ComprehensiveList.Res> resList = new ArrayList<>();
//        resList.add(new ComprehensiveList.Res("光明小学防火物资库","物资库","1"));//0 1待整治 2待检查/审核 3已完成
//        resList.add(new ComprehensiveList.Res("四方石蓄水罐","水源地","2"));//0 1待整治 2待检查/审核 3已完成
//        taskLists.add(new ComprehensiveList("20240729综合检查","青岛市/城阳区","进行中","2024-07-02 14:14:00","2024-07-02 14:14:00","张晓华",resList));
//        taskLists.add(new ComprehensiveList("20240729综合检查","青岛市/城阳区","进行中","2024-07-02 14:14:00","2024-07-02 14:14:00","张晓华",resList));
//        updateData();
    }

    public void updateData() {
        items.clear();
        if (taskLists != null && taskLists.size()!=0) {
            items.addAll(taskLists);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
