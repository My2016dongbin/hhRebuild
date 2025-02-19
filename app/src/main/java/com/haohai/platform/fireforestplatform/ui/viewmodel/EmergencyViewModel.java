package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MessageChange;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFilter;
import com.haohai.platform.fireforestplatform.ui.multitype.Emergency;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.builder.GetBuilder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class EmergencyViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public int pageNum = 1;
    public EmergencyFilter filter;
    public List<Emergency> messageList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }

    public void postData(boolean load){
        if(load){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        GetBuilder getBuilder = HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_PAGE)
                .addParams("pageNum", pageNum + "")
                .addParams("pageSize", "10")
                .addParams("incidentQryType", "2");
        if(filter!=null){
            if(filter.getTitle()!=null){
                getBuilder.addParams("incidentTitle",filter.getTitle());
            }
            if(filter.getAreaId()!=null){
                getBuilder.addParams("incidentArea",filter.getAreaId());
            }
            if(filter.getStartTime()!=null && filter.getEndTime()!=null){
                getBuilder.addParams("incidentTimeStart",filter.getStartTime());
                getBuilder.addParams("incidentTimeEnd",filter.getEndTime());
            }
            if(filter.getLevel()!=null){
                getBuilder.addParams("incidentLevel",filter.getLevel());
            }
            if(filter.getTypeId()!=null){
                getBuilder.addParams("incidentType",filter.getTypeId());
                String[] strings = filter.getTypeId().split("/");
                try{
                    getBuilder.addParams("remIncidentType",strings[0]);
                    getBuilder.addParams("remIncidentType",strings[1]);
                    getBuilder.addParams("remIncidentType",strings[2]);
                }catch (Exception e){
                    HhLog.e(e.getMessage());
                }
            }
        }
        HhLog.e("getBuilder: " + filter );
        getBuilder
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
                            messageList = new Gson().fromJson(String.valueOf(rows), new TypeToken<List<Emergency>>() {
                            }.getType());
                            updateData();

                        } catch (JSONException e) {
                            HhLog.e("get " + e.getMessage());
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
