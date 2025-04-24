package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.LaunchActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

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
        HhHttp.get()
                .url(URLConstant.GET_LOGIN)
                .addParams("username", userName)
                .addParams("password", password)
                .addParams("grant_type", "password")
                .addParams("client_id", "client_password")
                .addParams("client_secret", "123456")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: GET_LOGIN = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CommonData.token = jsonObject.getString("access_token");
                            SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                            SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                            SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                            doYXLogin();
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


    private void doYXLogin() {
        HhHttp.post()
                .url(URLConstant.YX_LOGIN)
                .addParams("accid",(String) SPUtils.get(context, SPValue.phone,""))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: YX_LOGIN = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject obj = (JSONObject) data.get(0);
                            JSONObject model = obj.getJSONObject("info");
                            String accid = model.getString("accid");
                            String token = model.getString("token");
                            CommonData.wyyAccId = accid;
                            CommonData.wyyToken = token;

                            LoginInfo info = new LoginInfo(accid,token);
                            RequestCallback<LoginInfo> callback =
                                    new RequestCallback<LoginInfo>() {
                                        @Override
                                        public void onSuccess(LoginInfo param) {
                                            // your code
                                            HhLog.e( "onSuccess: 网易云信login" +param);
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            HhLog.e( "网易云信login"+"onFailed code " + code);
                                            if (code == 302) {
                                                // your code
                                            } else {
                                                // your code
                                            }
                                        }

                                        @Override
                                        public void onException(Throwable exception) {
                                            // your code
                                            HhLog.e( "网易云信login"+"onException code " + exception);
                                        }
                                    };

                            //执行手动登录
                            NIMClient.getService(AuthService.class).login(info).setCallback(callback);

                        } catch (Exception e) {
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
