package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.LaunchActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.bean.request.LoginBean;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class LaunchViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }


    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "自动登录中.."));
        String content = new Gson().toJson(new LoginBean(null, CommonUtil.getMD5(password), userName));
        HhHttp.postString()
                .url(URLConstant.POST_LOGIN)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: content = " + content);
                        HhLog.e("onSuccess: login = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            CommonData.token = data.getString("access_token");
                            SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                            SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                            SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    ((LaunchActivity)context).finish();
                                }
                            },2000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SPUtils.put(context,SPValue.login,false);
                                context.startActivity(new Intent(context, LoginActivity.class));
                                ((LaunchActivity)context).finish();
                            }
                        },2000);
                    }
                });
    }
}
