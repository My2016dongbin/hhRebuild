package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.bean.PostIdentify;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.Handle;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class FgHandleViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Handle> messageList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public int size = 20;
    public String args;
    public void start(Context context){
        this.context = context;
    }

    public void postData(boolean load){
        if(load){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        PostIdentify postIdentify = new PostIdentify();
        postIdentify.setLimit(size);
        postIdentify.setPage(page);
        postIdentify.setIsAndroid(0);
        PostIdentify.DtoBean dto = new PostIdentify.DtoBean();
        dto.setStartTime("");
        dto.setEndTime("");
        dto.setIsHandle("1");
        dto.setIsReal("1");
        dto.setName("");
        dto.setAlarmType("");
        dto.setIp(0);
        dto.setIsDisposition(parseStatus(args));
        postIdentify.setDto(dto);
        HhHttp.postString()
                .url(URLConstant.POST_HANDLE_LIST)
                .content(new Gson().toJson(postIdentify))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_HANDLE_LIST" + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                JSONArray dataList = obj.getJSONArray("dataList");
                                messageList = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<Handle>>() {
                                }.getType());
                                updateData();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
        /*for (int m = 0; m < 10; m++) {
            int x = m%3;
            String status = "";
            String imageUrl = "";
            if(x == 0){
                status = "真实";
                imageUrl = "http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2023-12-06/Fire_Result/13/H9-FIR_2023-12-06_0700__13_T3.72um_T.jpg";
            }
            if(x == 1){
                status = "未处置";
                imageUrl = "http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/05/16/c702bd29de5946db87c694cf6500a833/O1CN01G3C7HZ1yHKG7s807x_!!1020886553.jpg";
            }
            if(x == 2){
                status = "疑似";
                imageUrl = "http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/05/16/8cc39e3dfa314ad7a3834f2fd94f4c55/O1CN01iiowXj1yHKGBNVv0e_!!1020886553.jpg";
            }
            messageList.add(new Handle(""+m,"青岛浩海网络科技股份有限公处置数据"+m,imageUrl,"2024-05-1"+m+" 22:33:00",status,"山东省青岛市城阳区物联网产业园浩海网络科技股份有限公司研发中心"));
        }
        updateData();*/

    }

    private String parseStatus(String args) {
        String tag = null;
        if(Objects.equals(args, "全部")){
            tag = null;
        }
        if(Objects.equals(args, "已处置")){
            tag = "1";
        }
        if(Objects.equals(args, "未处置")){
            tag = "0";
        }
        return tag;
    }


    public void updateData() {
        if(page == 1){
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
