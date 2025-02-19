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
import com.haohai.platform.fireforestplatform.ui.multitype.GridTrees;
import com.haohai.platform.fireforestplatform.ui.bean.VideoModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

public class FgVideoViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public int selectedIndex = 0;
    public boolean talking = false;
    public final MutableLiveData<Integer> viewCount = new MutableLiveData<>(1);
    public final MutableLiveData<Integer> viewNum = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> turnType = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> event = new MutableLiveData<>(0);
    public final MutableLiveData<List<VideoModel>> videoList = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }

    public void turnClick(int type){
        //Toast.makeText(context, "请先添加设备监控", Toast.LENGTH_SHORT).show();
        turnType.setValue(type);
    }
    public void addClick(int num){
        viewNum.setValue(num);
    }
    public void oneClick(View view){
        viewCount.setValue(1);
    }
    public void fourClick(View view){
        viewCount.setValue(4);
    }
    public void nineClick(View view){
        viewCount.setValue(9);
    }

    public void postMove(int type, int speed, int stop) {
        HhHttp.get()
                .url(URLConstant.GET_CONTROL)
                .addParams("monitorId", CommonData.videoDeleteMonitorId)
                .addParams("channelId", CommonData.videoDeleteChannelId)
                .addParams("speed", speed+"")
                .addParams("stop", stop+"")
                //.addParams("controlId", CommonData.videoDeleteControlId)
                .addParams("controlType", type+"")
                //.addParams("gridNo", (String) SPUtils.get(context, SPValue.gridNo,"370214"))
                //.addParams("groupId", (String) SPUtils.get(context, SPValue.groupId,"001021"))
                .addParams("gridNo", "370214")
                //.addParams("groupId", "001021")
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTROL " + response);
                        HhLog.e("GET_CONTROL monitorId " + CommonData.videoDeleteMonitorId);
                        HhLog.e("GET_CONTROL channelId " + CommonData.videoDeleteChannelId);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("GET_CONTROL e" + e.getMessage());
                    }
                });
    }
}
