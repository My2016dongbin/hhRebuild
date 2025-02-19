package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskListInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TaskListInfoViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
    public final MutableLiveData<TaskList> taskLists = new MutableLiveData<>();


    public void barLeftClick(View v){
        ((TaskListInfoActivity)context).finish();
    }

    public void postData(String ids){
        HhLog.e("ids " + ids);
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_TASK_INFO)
                //.addHeader("NetworkType","Internet")
                .addParams("id",ids)
                //.addParams("type","appInternet")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.postValue(new LoadingEvent(false));
                        HhLog.e("postData GET_TASK_INFO " + ids);
                        HhLog.e("postData GET_TASK_INFO response" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if(Objects.equals(code, "200")){
                                JSONObject data = jsonObject.getJSONObject("data");
                                TaskList taskList = new Gson().fromJson(String.valueOf(data),TaskList.class);
                                taskLists.postValue(taskList);
                            }else{
                                //error();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        //loading.postValue(new LoadingEvent(false));
                        error();
                    }
                });
    }

    private void error() {
        Toast.makeText(context, "该任务数据异常,请稍后重试", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    ((TaskListInfoActivity)context).finish();
                }catch(Exception e){
                    Log.e("TAG", "run: " + e.getMessage() );
                }
            }
        },1000);
    }

    public void changeState(String ids){
        HhLog.e("ids " + ids);
        loading.postValue(new LoadingEvent(true,"加载中.."));
        CommonParams p = new CommonParams();
        TaskList value = taskLists.getValue();
        if(value == null || value.getTaskStatus() == null || Objects.equals(value.getTaskStatus(), "completed")){
            Toast.makeText(context, "任务已结束", Toast.LENGTH_SHORT).show();
            loading.postValue(new LoadingEvent(false));
            return;
        }
        if(Objects.equals(Objects.requireNonNull(value).getTaskStatus(), "wait")){
            p.setTaskStatus("progress");
        }else if(Objects.equals(value.getTaskStatus(), "progress")){
            p.setTaskStatus("completed");
        }else{
            p.setTaskStatus("progress");
        }
        p.setId(ids);
        HhLog.e("PUT_TASK_STATE " + new Gson().toJson(p));
        HhHttp.method("POST", URLConstant.PUT_TASK_STATE, new Gson().toJson(p), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                loading.postValue(new LoadingEvent(false));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                loading.postValue(new LoadingEvent(false));
                HhLog.e("PUT_TASK_STATE onResponse " + response.body().toString());
                postData(ids);
            }
        });
    }
}
