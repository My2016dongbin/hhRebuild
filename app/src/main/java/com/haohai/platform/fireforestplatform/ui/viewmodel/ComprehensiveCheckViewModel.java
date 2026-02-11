package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.haohai.platform.fireforestplatform.old.linyi.Grid;
import com.haohai.platform.fireforestplatform.old.linyi.Res;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveCheckActivity;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.DailyCheckActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Area;
import com.haohai.platform.fireforestplatform.ui.bean.CheckPerson;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResource;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.ComprehensiveSubmit;
import com.haohai.platform.fireforestplatform.ui.bean.VideoModel;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class ComprehensiveCheckViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public final MutableLiveData<List<CheckResource>> resourceList = new MutableLiveData<>();
    public final MutableLiveData<Integer> chooseType = new MutableLiveData<>(1);
    public List<Area> gridAllList = new ArrayList<>();
    public List<Area> gridList = new ArrayList<>();
    public List<Area> grid2List = new ArrayList<>();
    public List<Area> grid3List = new ArrayList<>();
    public List<CheckPerson> userList = new ArrayList<>();
    public int gridIndex = 0;
    public int grid2Index = 0;
    public int grid3Index = 0;
    public int userIndex = 0;
    public String title = "";
    public String info = "";
    public String endTime = "";
    public StringBuffer date = new StringBuffer();
    public StringBuffer endDate = new StringBuffer();
    public int year;
    public int month;
    public int day;
    public int chooseHour;
    public int chooseMinute;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ComprehensiveCheckActivity)context).finish();
    }
    public void barRightClick(View v){
        context.startActivity(new Intent(context, ComprehensiveListActivity.class));
    }

    /*public void postData(){
        try {
            gridList = new DbConfig(context).getDbManager().selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .and("level", "=", "1")
                    .and("gridno", "like", "37%")
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }*/
    public void postData() {
        DbConfig dbConfig = new DbConfig(context);
        gridAllList = dbConfig.getGridList();
        if(gridAllList!=null){
            initArea();
        }
        else{
            loading.setValue(new LoadingEvent(true, "数据加载中.."));
            HhHttp.get()
                    .url(URLConstant.GET_GRID)
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(this, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            HhLog.e("getGridData: " + URLConstant.GET_GRID);
                            HhLog.e("getGridData: " + CommonData.token);
                            HhLog.e("getGridData: " + response);
                            loading.setValue(new LoadingEvent(false, ""));
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray data = jsonObject.getJSONArray("data");
                                gridAllList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Area>>() {
                                }.getType());
                                //存储网格信息
                                DbManager db = dbConfig.getDbManager();
                                try {
                                    db.saveOrUpdate(gridList);
                                } catch (DbException e) {

                                }
                                initArea();

                            } catch (JSONException e) {
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
    }


    public void initArea() {
        gridList.clear();
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("1".equals(gridAllList.get(i).getLevel())) {
                gridList.add(gridAllList.get(i));
            }
        }
    }
    public void initArea2() {
        HhLog.e("initArea2 " + gridList.get(gridIndex).toString());
        grid2List.clear();
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("2".equals(gridAllList.get(i).getLevel()) && gridAllList.get(i).getParentId().equals(gridList.get(gridIndex).getId())) {
                grid2List.add(gridAllList.get(i));
            }
        }
    }
    public void initArea3() {
        grid3List.clear();
        HhLog.e("initArea3 " + grid2List.get(grid2Index).toString());
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("3".equals(gridAllList.get(i).getLevel()) && gridAllList.get(i).getParentId().equals(grid2List.get(grid2Index).getId())) {
                grid3List.add(gridAllList.get(i));
            }
        }
    }

    public void getUsers() {
        loading.setValue(new LoadingEvent(true,"加载中.."));
        JSONObject object = new JSONObject();
        try {
            object.put("gridNo","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.GET_USER_LIST)
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_USER_LIST " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            userList =  new Gson().fromJson(String.valueOf(data), new TypeToken<List<CheckPerson>>() {
                            }.getType());

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

    public void submit(Area grid) {
        ComprehensiveSubmit comprehensiveSubmit = new ComprehensiveSubmit();
        try{
            comprehensiveSubmit.setCheckStationCount(Objects.requireNonNull(resourceList.getValue()).size());
        }catch (Exception e){
            Toast.makeText(context, "请选择资源点", Toast.LENGTH_SHORT).show();
            return;
        }
        comprehensiveSubmit.setCheckUserName((String) SPUtils.get(context,SPValue.fullName,""));
        try{
            CheckPerson checkPerson = userList.get(userIndex);
            ComprehensiveSubmit.User user = new ComprehensiveSubmit.User();
            user.setPlanInResourceId("");
            user.setPlanInResourceType(3);
            user.setType(2);
            user.setUserId(checkPerson.getId());
            user.setUserName(checkPerson.getFullName());
            List<ComprehensiveSubmit.User> list = new ArrayList<>();
            list.add(user);
            comprehensiveSubmit.setCheckuserVOS(list);
        }catch (Exception e){
            Toast.makeText(context, "请选择整治人", Toast.LENGTH_SHORT).show();
            return;
        }
        comprehensiveSubmit.setDescription(info);
        comprehensiveSubmit.setName(title);
        comprehensiveSubmit.setType(3);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        comprehensiveSubmit.setStartTime(simpleDateFormat.format(new Date()));
        comprehensiveSubmit.setEndTime("2024-08-21 00:00:00");
        try{
            comprehensiveSubmit.setGridName(grid.getName());
            comprehensiveSubmit.setGridNo(grid.getId());
        }catch (Exception e){
            Toast.makeText(context, "请选择网格", Toast.LENGTH_SHORT).show();
            return;
        }
        comprehensiveSubmit.setPlanResourceDTOS(resourceList.getValue());
        String content = new Gson().toJson(comprehensiveSubmit);
        HhLog.e("POST_COMPREHENSIVE_SUBMIT content" + content);
        HhHttp.postString()
                .url(URLConstant.POST_COMPREHENSIVE_SUBMIT)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_COMPREHENSIVE_SUBMIT " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 200){
                                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                                ((ComprehensiveCheckActivity)context).finish();
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
}
