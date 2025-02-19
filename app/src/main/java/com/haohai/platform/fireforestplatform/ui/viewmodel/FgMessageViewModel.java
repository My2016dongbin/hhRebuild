package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import android.content.Context;
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
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class FgMessageViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Message> messageList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }

    public void postData(boolean load){
        if(load){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        HhHttp.postString()
                //.url(URLConstant.POST_MESSAGE)
                .url(URLConstant.POST_MESSAGE_NEW)
                .content(new Gson().toJson(new JSONObject()))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_MESSAGE_NEW" + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                String unreadTotal = obj.getString("unreadTotal");
                                JSONArray appMessageList = obj.getJSONArray("AppMessageList");
                                messageList = new Gson().fromJson(String.valueOf(appMessageList), new TypeToken<List<Message>>() {
                                }.getType());
                                updateData();
                                EventBus.getDefault().post(new MessageChange(unreadTotal));
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
    }
    public void clearMessage(){
        String val = String.valueOf(SPUtils.get(context, SPValue.id, ""));
        HhHttp.get()
                .url(URLConstant.GET_MESSAGE_CLEAR)
                .addParams("userId", val)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("clear " + val + response);
                        loading.setValue(new LoadingEvent(false));
                        if(response.contains(":200")){
                            Toast.makeText(context, "消息已清除", Toast.LENGTH_SHORT).show();
                        }
                        EventBus.getDefault().post(new MessageRefresh());
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        EventBus.getDefault().post(new MessageRefresh());
                    }
                });
    }


    public void updateData() {
        items.clear();
        if (messageList != null && messageList.size()!=0) {
            items.addAll(messageList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
