package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.Ext;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.ChangePassWordActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.HhToast;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePassWordViewModel extends BaseViewModel {
    public Context context;
    public List<News> newsList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ChangePassWordActivity)context).finish();
    }

    public void submit(String oldStr,String newStr){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhLog.e("submit oldPassword - " + CommonUtil.getMD5(oldStr));
        HhLog.e("submit newPassword - " + CommonUtil.getMD5(newStr));
        RequestParams requestParams = new RequestParams(URLConstant.PUT_CHANGE_PASSWORD);
        requestParams.addParameter("oldPassword", CommonUtil.getMD5(oldStr));
        requestParams.addParameter("newPassword", CommonUtil.getMD5(newStr));
        HhHttp.methodX(HttpMethod.PUT, requestParams, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String response) {
                HhLog.e("submit oldPassword " + CommonUtil.getMD5(oldStr));
                HhLog.e("submit newPassword " + CommonUtil.getMD5(newStr));
                HhLog.e("submit " + response);
                //{"msg":"操作成功","code":200}
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
//                    Looper.prepare();
                    if(Objects.equals(code, "200")){
                        HhLog.e("submit code " + code);
                        HhLog.e("submit msg " + msg);
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();


                        SPUtils.put(context,SPValue.login,false);
                        CommonData.token = "";
                        CommonData.videoAddingIndex = 0;
                        CommonData.videoDeleteIndex = 0;
                        CommonData.videoDeleteMonitorId = "";
                        CommonData.videoDeleteChannelId = "";
                        CommonData.videoPlayingIndexList = new ArrayList<>();
                        CommonData.videoDeleteModelList = new ArrayList<>();
                        CommonData.mainTabIndex = 0;
                        CommonData.walkDistance = 0;
                        SPUtils.put(context,SPValue.token,"");
                        SPUtils.put(HhApplication.getInstance(), SPValue.password, newStr);
                        context.startActivity(new Intent(context,LoginActivity.class));
                        EventBus.getDefault().post(new Ext());
                        ((ChangePassWordActivity)context).finish();
                    }else{
                        HhLog.e("submit code " + code);
                        HhLog.e("submit msg " + msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
//                    Looper.loop();


                } catch (Exception e) {
                    HhLog.e("submit e " + e.toString());
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
                loading.postValue(new LoadingEvent(false));
            }
        });
        /*HhHttp.methodNoApp("PUT", URLConstant.PUT_CHANGE_PASSWORD, new Gson().toJson(new CommonParams("", oldStr, newStr)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loading.postValue(new LoadingEvent(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                HhLog.e("submit " + string);
                loading.postValue(new LoadingEvent(false));
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    Looper.prepare();
                    if(Objects.equals(code, "200")){
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        ((ChangePassWordActivity)context).finish();
                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        */
    }
}
