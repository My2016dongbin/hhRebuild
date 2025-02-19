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
import com.haohai.platform.fireforestplatform.ui.bean.ReadParam;
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
import java.util.Objects;
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
        HhHttp.get()
                //.url(URLConstant.POST_MESSAGE)
                .url(URLConstant.POST_MESSAGE_NEW)
                //.content(new Gson().toJson(new JSONObject()))
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
                                //JSONObject obj = (JSONObject) data.get(0);
                                //String unreadTotal = obj.getString("unreadTotal");
                                //JSONArray appMessageList = obj.getJSONArray("AppMessageList");
                                messageList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Message>>() {
                                }.getType());
                                //EventBus.getDefault().post(new MessageChange(unreadTotal));
                                getReadUnRead();
                            }
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

    private void getReadUnRead() {
        HhHttp.get()
                .url(URLConstant.GET_MESSAGE_COUNT)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_MESSAGE_COUNT" + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            String unread = "0";
                            try{
                                unread = data.getString("unread");
                            }catch(Exception e){
                                HhLog.e(e.getMessage());
                            }


                            EventBus.getDefault().post(new MessageChange(unread));

                        } catch (JSONException e) {
                            HhLog.e("GET_MESSAGE_COUNT"+e.getMessage());
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
        loading.setValue(new LoadingEvent(true,"正在清除.."));
        HhHttp.postString()
                .url(URLConstant.POST_MESSAGE_CLEAR)
                .content(new JSONObject().toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess POST_MESSAGE_CLEAR response " + response);
                        loading.setValue(new LoadingEvent(false));
                        if(response.contains(":200")){
                            Toast.makeText(context, "消息已清除", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "消息清除失败请重试", Toast.LENGTH_SHORT).show();
                        }
                        EventBus.getDefault().post(new MessageRefresh());
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure POST_MESSAGE_CLEAR response " + e.toString());
                        Toast.makeText(context, "消息清除失败请重试", Toast.LENGTH_SHORT).show();
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

    public void postRead(Message message){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        String read = new Gson().toJson(new ReadParam(message.getId(), "read"));
        HhHttp.postString()
                .url(URLConstant.GET_CHANGE_STATE_NEW)
                .content(read)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.postValue(new LoadingEvent(false));
                        HhLog.e("GET_CHANGE_STATE_NEW " + URLConstant.GET_CHANGE_STATE_NEW);
                        HhLog.e("GET_CHANGE_STATE_NEW " + read);
                        HhLog.e("GET_CHANGE_STATE_NEW " + response);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.postValue(new LoadingEvent(false));
                    }
                });
    }
}
