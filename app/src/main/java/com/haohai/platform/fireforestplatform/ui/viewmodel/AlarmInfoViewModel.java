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
import com.haohai.platform.fireforestplatform.ui.activity.AlarmInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Audit;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyDetail;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyIndex;
import com.haohai.platform.fireforestplatform.ui.bean.ExamineBean;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class AlarmInfoViewModel extends BaseViewModel {
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
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((AlarmInfoActivity)context).finish();
    }

    public void pass_() {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("noticeId", CommonData.alarm.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.POST_NOTICE_FEEDBACK)
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_NOTICE_FEEDBACK " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "已确认", Toast.LENGTH_SHORT).show();
                            ((AlarmInfoActivity)context).finish();

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
