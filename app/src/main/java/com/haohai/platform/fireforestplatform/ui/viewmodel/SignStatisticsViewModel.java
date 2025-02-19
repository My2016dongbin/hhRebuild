package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.activity.SignStatisticsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.SignModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

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
    public String attendanceStatus = "";//"" 全部  "1" 有考勤  "0" 无考勤
    public int limit = 1000;
    public int page = 1;
    public int all_person = 0;
    public int sign_person = 0;
    public int walk_distance = 0;
    public int sign_count = 0;
    public List<SignModel> signLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public final MutableLiveData<Integer> updateState = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((SignStatisticsActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String content = new Gson().toJson(new CommonParams(limit,page,new CommonParams.Dto(date.toString(),endDate.toString(),new ArrayList<>(),attendanceStatus)));
        HhHttp.postString()
                .url(URLConstant.POST_SIGN_STATISTICS)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e(content);
                        HhLog.e(response);
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
        if (signLists != null && signLists.size()!=0) {
            items.addAll(signLists);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
