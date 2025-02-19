package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.bean.ExamineBean;
import com.haohai.platform.fireforestplatform.ui.multitype.Examine;
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

public class ExamineViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public int pageNum = 1;
    public List<Examine> messageList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }

    public void postData(boolean load){
        if(load){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_PAGE)
                .addParams("pageNum",pageNum+"" )
                .addParams("pageSize","10")
                .addParams("incidentQryType","1")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_EMERGENCY_PAGE " +  pageNum + "," + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            messageList = new Gson().fromJson(String.valueOf(rows), new TypeToken<List<Examine>>() {
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
    }

    public void pass_(String inputStr,Examine message,String pass) {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        ExamineBean examineBean = new ExamineBean(message.getId(), pass, inputStr);//auditResult  pass通过 reject驳回
        HhHttp.postString()
                .url(URLConstant.POST_EMERGENCY_AUDIT)
                .content(new Gson().toJson(examineBean))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_EMERGENCY_AUDIT " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "审核已提交", Toast.LENGTH_SHORT).show();
                            postData(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Toast.makeText(context, "审核已提交", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateData() {
        if(pageNum == 1){
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
