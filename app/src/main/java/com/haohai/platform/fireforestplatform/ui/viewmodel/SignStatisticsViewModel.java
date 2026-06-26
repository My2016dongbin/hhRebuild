package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.SignStatisticsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.StatisticsParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.SignModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.zhy.http.okhttp.builder.GetBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class SignStatisticsViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public StringBuffer date = new StringBuffer();//"2023-12-01 00:00:00"
    public StringBuffer endDate = new StringBuffer();//"2023-12-31 23:59:59"
    public int year = 2023;
    public int month = 12;
    public int day = 1;
    public String attendanceStatus = "";//"" 全部  "HAS" 有考勤  "NO" 无考勤
    public int limit = 1000;
    public int page = 1;
    public int cityUserNum = 0;
    public int areaUserNum = 0;
    public int gridUserNum = 0;
    public int lawUserNum = 0;
    public int abnormalNum = 0;
    public int all_person = 0;
    public int sign_person = 0;
    public int walk_distance = 0;
    public int sign_count = 0;
    public boolean hasMoreData = true;
    public List<SignModel> signLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public final MutableLiveData<Integer> updateState = new MutableLiveData<>();
    public final MutableLiveData<Integer> refreshEvent = new MutableLiveData<>();
    public final MutableLiveData<Integer> loadMoreEvent = new MutableLiveData<>();
    public final MutableLiveData<Boolean> noMoreData = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((SignStatisticsActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        GetBuilder url = HhHttp.get()
                .url(URLConstant.POST_SIGN_STATISTICS_NEW);
        url.addParams("current",page+"")
                .addParams("size",limit+"")
                .addParams("startDate",date.toString())
                .addParams("endDate",endDate.toString())
                .addParams("attendanceFilter",attendanceStatus);
        Log.e("TAG","POST_SIGN_STATISTICS_NEW url " + " page , " + page
                + " size , " + limit
                + " startDate , " + date.toString()
                + " endDate , " + endDate.toString()
                + " attendanceFilter , " + attendanceStatus);
        url.build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_SIGN_STATISTICS_NEW response " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = parseDataObject(jsonObject);
                            if (data == null) {
                                cityUserNum = 0;
                                areaUserNum = 0;
                                gridUserNum = 0;
                                lawUserNum = 0;
                                abnormalNum = 0;
                                hasMoreData = false;
                                signLists = new ArrayList<>();
                                notifyPageResult();
                                updateState.postValue(new Random().nextInt(1000));
                                updateData();
                                return;
                            }
                            cityUserNum = data.optInt("cityUserNum");
                            areaUserNum = data.optInt("areaUserNum");
                            gridUserNum = data.optInt("gridUserNum");
                            lawUserNum = data.optInt("lawUserNum");
                            abnormalNum = data.optInt("abnormalNum");
                            JSONObject pageData = data.optJSONObject("userAttendanceItemPage");
                            JSONArray dataList = pageData == null ? null : pageData.optJSONArray("records");
                            signLists = dataList == null ? new ArrayList<>() : new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<SignModel>>() {
                            }.getType());
                            hasMoreData = signLists.size() >= limit;
                            for (int i = 0; i < signLists.size(); i++) {
                                SignModel signModel = signLists.get(i);
                                signModel.setIndex((page-1)*limit+i+1);
                            }
                            notifyPageResult();
                            updateState.postValue(new Random().nextInt(1000));
                            updateData();

                        } catch (JSONException e) {
                            onPageFailure();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                        onPageFailure();
                        HhLog.e("POST_SIGN_STATISTICS_NEW e " + e.getMessage());
                    }
                });
    }

    private void notifyPageResult() {
        if (page == 1) {
            refreshEvent.postValue(new Random().nextInt(1000));
        } else {
            loadMoreEvent.postValue(new Random().nextInt(1000));
        }
        noMoreData.postValue(!hasMoreData);
    }

    private void onPageFailure() {
        if (page > 1) {
            page--;
            loadMoreEvent.postValue(new Random().nextInt(1000));
        } else {
            refreshEvent.postValue(new Random().nextInt(1000));
        }
    }

    private JSONObject parseDataObject(JSONObject jsonObject) {
        Object data = jsonObject.opt("data");
        if (data instanceof JSONObject) {
            return (JSONObject) data;
        }
        if (data instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) data;
            if (jsonArray.length() > 0) {
                return jsonArray.optJSONObject(0);
            }
        }
        return null;
    }
    public void postDataOld(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String content = new Gson().toJson(new StatisticsParams(page,limit,new StatisticsParams.Dto(date.toString(),endDate.toString(),new ArrayList<>(),attendanceStatus)));
        HhHttp.postString()
                .url(URLConstant.POST_SIGN_STATISTICS)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_SIGN_STATISTICS " + content);
                        HhLog.e("POST_SIGN_STATISTICS " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray dataList = data.getJSONArray("dataList");
                            all_person = data.getInt("totalSize");
                            signLists = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<SignModel>>() {
                            }.getType());
                            for (int i = 0; i < signLists.size(); i++) {
                                SignModel signModel = signLists.get(i);
                                signModel.setIndex((page-1)*limit+i+1);
                                if(signModel.getLastPatrolDate()!=null){
                                    sign_person++;
                                }
                                walk_distance = signModel.getTotalPatrolLength() + walk_distance;
                                sign_count = signModel.getAttendanceTimes() + sign_count;
                            }
                            updateState.postValue(new Random().nextInt(1000));
                            updateData();

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

    public void updateData() {
        if(page==1){
            items.clear();
        }
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i) instanceof Empty) {
                items.remove(i);
            }
        }
        if (signLists != null && signLists.size()!=0) {
            items.addAll(signLists);
        }else{
            if(page==1){
                items.add(new Empty());
            }
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
