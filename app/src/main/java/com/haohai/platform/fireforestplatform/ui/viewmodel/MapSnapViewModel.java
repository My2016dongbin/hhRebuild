package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.LiveUploadActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MapSnapActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.MineMenu;
import com.haohai.platform.fireforestplatform.ui.bean.SnapModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;

public class MapSnapViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<List<SnapModel>> snapModelList = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void barLeftClick(View v){
        ((MapSnapActivity)context).finish();
    }

    public void postTaskRoom(String roomId) {
        HhHttp.postString()
                .url(URLConstant.POST_TASK_ROOM)
                .content(new Gson().toJson(new CommonParams(0,"","",roomId)))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if(data.length()>0){
                                    JSONObject obj = (JSONObject) data.get(0);
                                    String snapId = obj.getString("snapId");
                                    getSnapData(snapId);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }
    public void getSnapData(String snapId) {
        HhHttp.get()
                .url(URLConstant.GET_TASK_SNAP)
                .addParams("id",snapId)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        int index = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if(data.length()>0){
                                    JSONObject obj = (JSONObject) data.get(0);
                                    String content = obj.getString("content");
                                    JSONArray jsonArray = new JSONArray(content);
                                    List<SnapModel> modelList = new ArrayList<>();
                                    HhLog.e("length = " + jsonArray.length());
                                    HhLog.e("jsonArray = " + jsonArray);
                                    if(jsonArray.length()>0){
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            index = i;
                                            Gson gson = new Gson();
                                            JSONObject o = (JSONObject) jsonArray.get(i);
                                            String type = o.getString("type");
                                            String geometry = o.getString("geometry");
                                            String properties = o.getString("properties");
                                            JSONObject geo = new JSONObject(geometry);
                                            SnapModel.Properties pro = gson.fromJson(properties, SnapModel.Properties.class);
                                            modelList.add(new SnapModel(geo,type,pro));
                                        }
                                        snapModelList.postValue(modelList);
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            HhLog.e("error " + index + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

}
