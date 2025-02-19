package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.ModelActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Audit;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyDetail;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyIndex;
import com.haohai.platform.fireforestplatform.ui.bean.ExamineBean;
import com.haohai.platform.fireforestplatform.ui.bean.MineMenu;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.multitype.Emergency;
import com.haohai.platform.fireforestplatform.ui.multitype.Examine;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class EmergencyDetailViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public String id;
    public int page = 1;
    public int size = 10;
    public List<EmergencyIndex> dataList = new ArrayList<>();
    public final MutableLiveData<EmergencyDetail> detailChanged = new MutableLiveData<>();
    public final MutableLiveData<Integer> examineTag = new MutableLiveData<>();
    public final MutableLiveData<List<Audit>> auditData = new MutableLiveData<>();
    public List<Audit> auditList = new ArrayList<>();
    public EmergencyDetail emergencyDetail;
    public EmergencyIndex emergencyIndex;
    public List<TypeBean> areaList = new ArrayList<>();
    public List<TypeBean> levelList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((EmergencyDetailActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_DETAIL_LIST)
                .addParams("incidentId",id)
                .addParams("pageNum",page+"")
                .addParams("pageSize",size+"")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_EMERGENCY_DETAIL_LIST" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("rows");
                            dataList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<EmergencyIndex>>() {
                            }.getType());

                            if(dataList.size()>0){
                                emergencyIndex = dataList.get(0);
                                getInfo();
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
    public void getInfo(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        if(emergencyIndex==null){
            return;
        }
        HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_DETAIL + "/" + id + "/" + emergencyIndex.getIncidentVersionId())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_EMERGENCY_DETAIL" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            emergencyDetail = new Gson().fromJson(String.valueOf(data),EmergencyDetail.class);

                            detailChanged.postValue(emergencyDetail);

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
    public void getInfoNew(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_DETAIL_NEW)
                .addParams("incidentId",id)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_EMERGENCY_DETAIL_NEW success " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            emergencyDetail = new Gson().fromJson(String.valueOf(data),EmergencyDetail.class);

                            detailChanged.postValue(emergencyDetail);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_EMERGENCY_DETAIL_NEW error " + e.toString());
                    }
                });
    }
    public void getAuditInfo(){
        HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_AUDIT)
                .addParams("incidentId",id)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_EMERGENCY_AUDIT response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            auditList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Audit>>() {
                            }.getType());
                            auditData.postValue(auditList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {

                    }
                });
    }

    public void pass_(String inputStr, String pass) {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        ExamineBean examineBean = new ExamineBean(emergencyDetail.getId(), pass, inputStr);//auditResult  pass通过 reject驳回
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
//                            postData();
                            getInfoNew();
                            examineTag.postValue(1);

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
}
