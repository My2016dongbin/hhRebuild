package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.activity.UploadActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.PostStar;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.Upload;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class UploadViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public List<Upload> taskLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public int size = 10;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((UploadActivity)context).finish();
    }

    public void postData(){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_EVENT_UPLOAD_PAGE)
                .addParams("pageNum",page+"")
                .addParams("pageSize",size + "")
                .addParams("incidentQryType", "9")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_EVENT_UPLOAD_PAGE " + response);
                        loading.postValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("rows");
                            taskLists = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Upload>>() {
                            }.getType());
                            updateData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateData();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.postValue(new LoadingEvent(false));
                        updateData();
                    }
                });
    }

    public void updateData() {
        if(page == 1){
            items.clear();
        }
        if (taskLists != null && taskLists.size()!=0) {
            items.addAll(taskLists);
        }else{
            if(page == 1){
                items.add(new Empty());
            }
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    public void delete(Upload message) {
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.method("DELETE", URLConstant.DELETE_EVENT_UPLOAD_DELETE + message.getId(), "", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loading.postValue(new LoadingEvent(false));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        loading.postValue(new LoadingEvent(false));
                        HhLog.e("DELETE_EVENT_UPLOAD_DELETE " + response.message());
                        if(response.message().equals("OK")){
                            Looper.prepare();
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            page = 1;
                            postData();
                        }
                    }
                });
    }
}
