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
import com.haohai.platform.fireforestplatform.ui.activity.WorkerActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

public class WorkerViewModel extends BaseViewModel {
    public Context context;
    public List<WorkerDetail.Worker> workerList = new ArrayList<>();
    public List<WorkerDetail> workerDetailList = new ArrayList<>();
    public final MutableLiveData<List<WorkerDetail>> workerDetails = new MutableLiveData<>();
    public final MutableLiveData<Integer> monthChoose = new MutableLiveData<>();
    public int monthTab;
    public int mYear;
    public int currentYear = 2024;
    public int currentMonth = 1;
    public int currentDay = 1;
    public String gridNo;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((WorkerActivity)context).finish();
    }

    public void monthClick(int month){
        monthTab = month;
        monthChoose.postValue(month);
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));

//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.get()
                .url(URLConstant.GET_WORK_MONTH_GRID+currentYear+"-"+ CommonUtil.parseZero(currentMonth))
                .addParams("gridNo",gridNo)
//                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_WORK_MONTH_GRID " + response);
                        try {
                            workerDetailList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            workerList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<WorkerDetail.Worker>>() {
                            }.getType());

                            WorkerDetail workerDetail = new WorkerDetail();
                            Gson gson = new Gson();
                            for (int i = 0; i < workerList.size(); i++) {
                                WorkerDetail.Worker worker = workerList.get(i);
                                ///第一个一定加入
                                if(workerDetailList.isEmpty()) {
                                    WorkerDetail o = new WorkerDetail();
                                    ArrayList<WorkerDetail.Worker> list = new ArrayList<>();
                                    list.add(worker);
                                    o.setList(list);
                                    workerDetailList.add(o);
                                }
                                ///如果原数据已有当前天的时间 则加入 待下一个非当天时间时保存
                                if(Objects.requireNonNull(workerDetail).toString().contains("["+worker.getDutyDate()+"]")){
                                    workerDetail.appendList(worker);
                                    //如果最后一个 直接保存
                                    if(i == workerList.size()-1){
                                        workerDetailList.add(gson.fromJson(gson.toJson(workerDetail),WorkerDetail.class));
                                        workerDetail = null;
                                    }
                                }else{
                                    ///新时间-非原数据当天时间 先保存之前数据
                                    workerDetailList.add(gson.fromJson(gson.toJson(workerDetail),WorkerDetail.class));
                                    ///再清空
                                    //非最后一个 则清空 待下个数据
                                    if(i != workerList.size()-1){
                                        workerDetail = new WorkerDetail();
                                        workerDetail.appendList(worker);
                                    }else{
                                        //最后一个 添加保存
                                        workerDetail.appendList(worker);
                                        workerDetailList.add(gson.fromJson(gson.toJson(workerDetail),WorkerDetail.class));
                                        workerDetail = null;
                                    }

                                }
                            }
                            workerDetails.postValue(workerDetailList);


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


}
