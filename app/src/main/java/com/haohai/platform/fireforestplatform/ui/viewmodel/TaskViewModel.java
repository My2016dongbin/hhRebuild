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
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class TaskViewModel extends BaseViewModel {
    public static final String STATUS_NOT_STARTED = "0";
    public static final String STATUS_IN_PROGRESS = "1";
    public static final String STATUS_FINISHED = "2";

    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public List<TaskList> taskLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    ///0未开始，1执行中，2已结束
    public final MutableLiveData<String> status = new MutableLiveData<>(STATUS_NOT_STARTED);
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((TaskActivity)context).finish();
    }

    public void switchStatus(String taskStatus) {
        status.setValue(taskStatus);
        postData();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String content = new Gson().toJson(new CommonParams(id,status.getValue(),"", (String) SPUtils.get(context, SPValue.groupId, ""),"appInternet",new ArrayList<>()));
        HhHttp.postString()
                .url(URLConstant.POST_TASK_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        //HhLog.e(response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            taskLists = new Gson().fromJson(String.valueOf(data), new TypeToken<List<TaskList>>() {
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
