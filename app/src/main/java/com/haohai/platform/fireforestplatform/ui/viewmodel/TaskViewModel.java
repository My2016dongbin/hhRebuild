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
import com.haohai.platform.fireforestplatform.ui.bean.TaskPageParams;
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

public class TaskViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public List<TaskList> taskLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 20;
    public final MutableLiveData<Integer> loadMore = new MutableLiveData<>();

    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((TaskActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String groupId = (String) SPUtils.get(context, SPValue.groupId, "");
        TaskPageParams.Dto dto = new TaskPageParams.Dto(groupId, "", "", "", "");
        String content = new Gson().toJson(new TaskPageParams(limit, page, dto));
        HhHttp.postString()
                .url(URLConstant.POST_TASK_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_TASK_LIST " + page + ", " + content);
                        HhLog.e("POST_TASK_LIST " + page + ", " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            int totalSize = data.optInt("totalSize");
                            JSONArray dataList = data.optJSONArray("dataList");
                            if(dataList != null){
                                taskLists = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<TaskList>>() {
                                }.getType());
                            }else{
                                taskLists = new ArrayList<>();
                            }

                            updateData();
                            if(items.size() >= totalSize || taskLists.size() < limit){
                                loadMore.postValue(0);
                            }else{
                                loadMore.postValue(1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            HhLog.e("POST_TASK_LIST error " + e.getMessage());
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
        if (taskLists != null && taskLists.size()!=0) {
            items.addAll(taskLists);
        }else if(page==1){
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
