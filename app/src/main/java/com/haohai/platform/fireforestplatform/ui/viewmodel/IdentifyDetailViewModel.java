package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.IdentifyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.ui.cell.PostCheck;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

public class IdentifyDetailViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public String id;
    public Identify identify = new Identify();
    public HandleResult handleResult = new HandleResult();
    public CheckResult checkResult = new CheckResult();
    public final MutableLiveData<Identify> identifyDetail = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((IdentifyDetailActivity)context).finish();
    }

    public void getInfo(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_IDENTIFY_DETAIL)
                .addParams("id",id)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_IDENTIFY_DETAIL" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                identify = new Gson().fromJson(String.valueOf(obj),Identify.class);
                                identifyDetail.postValue(identify);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));

                        /*//TODO 测试数据
                        identify = new Identify();
                        identify.setMonitor("鄂前昂玛20森林防火");
                        identify.setWarnType("算法识别报警");
                        identify.setWarnTime("2023-08-08 08:00:00");
                        identify.setWarnLngLat("38.131221,130.576523");
                        identify.setWarnAddress("内蒙鄂托克前旗昂素镇玛拉迪社区基站");
                        identify.setAngle("0,0");
                        identify.setReal("待确认");
                        identify.setRemark("这是一条识别详情备注信息");
                        identify.setWarnImage("http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/05/16/c702bd29de5946db87c694cf6500a833/O1CN01G3C7HZ1yHKG7s807x_!!1020886553.jpg,http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2023-12-06/Fire_Result/13/H9-FIR_2023-12-06_0700__13_T3.72um_T.jpg");
                        identify.setLiveVideo("http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/05/16/08a1931e81b8445a8de95a811be65711/慧眼卫星审核.mp4,http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/05/16/cf3af9e54607491381c96d6771362b3f/1185_1715838072.mp4");
                        List<Identify.Line> lineList = new ArrayList<>();
                        lineList.add(new Identify.Line("执行人检查","1"));
                        lineList.add(new Identify.Line("反馈检查结果到负责人","1"));
                        lineList.add(new Identify.Line("进行整改并反馈","1"));
                        lineList.add(new Identify.Line("整改未达标","2","负责人：张晓华"));
                        lineList.add(new Identify.Line("进行整改并反馈","1"));
                        lineList.add(new Identify.Line("确认整改情况","0"));
                        lineList.add(new Identify.Line("任务结束","0"));
                        identify.setHandleLine(lineList);
                        identifyDetail.postValue(identify);*/
                    }
                });
    }

    public String parseStatus(Identify message) {
        String result;
        if(Objects.equals(message.getIsHandle(), "1")){
            if(Objects.equals(message.getIsReal(), "1")){
                result = "真实";
            }else{
                result = "疑似";
            }
        }else{
            result = "未处理";
        }
        return result;
    }

    public void postHandle() {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        RequestParams params = new RequestParams(URLConstant.PUT_IDENTIFY_HANDLE);
        params.addParameter("fireId",id);
        params.addParameter("isReal",handleResult.isReal()?1:0);
        params.addParameter("isTrueNote",handleResult.getRemark());
        HhHttp.methodX(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("PUT_IDENTIFY_HANDLE" + params.toString());
                HhLog.e("PUT_IDENTIFY_HANDLE" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                    getInfo();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void postCheck() {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        String content = new Gson().toJson(new PostCheck(id, checkResult.getRemark()));
        HhHttp.postString()
                .url(URLConstant.POST_IDENTIFY_CHECK)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("POST_IDENTIFY_CHECK" + content);
                        HhLog.e("POST_IDENTIFY_CHECK" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            getInfo();

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
