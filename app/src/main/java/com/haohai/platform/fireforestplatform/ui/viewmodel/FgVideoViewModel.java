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

    public void postMove(String type, int speed, int stop) {
        HhHttp.get()
                .url(URLConstant.GET_CONTROL_GB)
                .addParams("deviceId", CommonData.videoCtrlDeviceId)
                .addParams("serial", CommonData.videoCtrlDeviceId)
                .addParams("speed", speed+"")
                .addParams("controlType", type+"")
//                .addParams("controlId", "039b7481-e0f3-4fb5-bd0f-261870ff4f9f")
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTROL " + URLConstant.GET_CONTROL + "?deviceId="+CommonData.videoCtrlDeviceId+"&serial="+CommonData.videoCtrlDeviceId
                        +"&speed="+speed+"$controlType="+type);
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
    public void postMoveOld(int type, int speed, int stop) {
        HhHttp.get()
                .url(URLConstant.GET_CONTROL)
                .addParams("monitorId", CommonData.videoDeleteMonitorId)
                .addParams("channelId", CommonData.videoDeleteChannelId)
                .addParams("speed", speed+"")
                .addParams("stop", stop+"")
                //.addParams("controlId", "81ea0105-d779-4379-8af7-88759913617e"/*CommonData.videoDeleteControlId*/)
                .addParams("controlType", type+"")
                .addParams("gridNo", /*"150600"*/(String) SPUtils.get(context,SPValue.gridNo,"150600"))
                .addParams("groupId", /*"001"*/(String) SPUtils.get(context,SPValue.groupId,"001"))
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTROL " + URLConstant.GET_CONTROL + "?monitorId="+CommonData.videoDeleteMonitorId+"&channelId="+CommonData.videoDeleteChannelId
                        +"&speed="+speed+"$stop="+stop+"$controlType="+type+"&gridNo="+(String) SPUtils.get(context,SPValue.gridNo,"150600")+"&groupId="+(String) SPUtils.get(context,SPValue.groupId,"001"));
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
