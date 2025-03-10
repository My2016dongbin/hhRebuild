package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.genew.base.net.base.OnRequestResultListener;
import com.genew.base.net.bean.NiuxinLoginInfo;
import com.genew.base.net.bean.NiuxinResultInfo;
import com.genew.base.utils.ToastUtils;
import com.genew.mpublic.router.api.Api;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.nusdk.ErrorCode;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.LaunchActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.koushikdutta.ion.Response;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;

public class LoginViewModel extends BaseViewModel {
    public Context context;

    public void start(Context context) {
        this.context = context;
    }

    OnRequestResultListener onLoginResultListener = new OnRequestResultListener() {

        @Override
        public void onResult(Response<String> response, NiuxinResultInfo info) {
            // 登录出现异常错误
            if (info == null) {
                ((LoginActivity) context).runOnUiThread(() ->
                {
                    ToastUtils.xxxif(R.string.toast_login_fail_by_network);
                });
            } else if (response != null) {
                NiuxinLoginInfo loginInfo = (NiuxinLoginInfo) info;

                if (loginInfo.isLoginSuccessed()) {
                    //TODO 测试 登录成功 主页跳转
                    /*startActivity(new Intent(LaunchActivity.this,
                            MainActivity.class));
                    finish();*/
                    //context.startActivity(new Intent(context,MainActivity.class));
                } else {
                    ((LoginActivity) context).runOnUiThread(() ->
                    {
                        String error = ErrorCode.getNiuxinErrorString(
                                loginInfo.status.code);
                        //ToastUtils.xxxdo(context.getString(R.string.toast_login_fail_by_code) + error);
                    });
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!CommonData.token.isEmpty()) {
                            ((LoginActivity) context).finish();
                        }
                    }
                }, 5000);
            }
        }
    };

    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "登录中.."));
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
                        Log.e("TAG", "onSuccess: login = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CommonData.token = jsonObject.getString("access_token");
                            CommonData.sessionKey = jsonObject.getString("sessionKey");
                            SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                            SPUtils.put(HhApplication.getInstance(), SPValue.sessionKey, CommonData.sessionKey);
                            SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                            SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                            getUserInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());//400 密码错误  401 账号未注册
                        if (Objects.requireNonNull(e.getMessage()).contains("400")) {
                            msg.setValue("账号或密码错误");
                        } else if (Objects.requireNonNull(e.getMessage()).contains("401")) {
                            msg.setValue("账号未注册");
                        } else {
                            msg.setValue("");
                        }
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    private void getUserInfo() {
        HhHttp.get()
                .url(URLConstant.GET_USER_INFO)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: userInfo = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                JSONObject userJsonObj = data.getJSONObject(0);
                                SPUtils.put(HhApplication.getInstance(), SPValue.id, userJsonObj.getString("id"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.userCode, userJsonObj.getString("userCode"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.fullName, userJsonObj.getString("fullName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.phone, userJsonObj.getString("phone"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.email, userJsonObj.getString("email"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupId, userJsonObj.getString("groupId"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.gridNo, userJsonObj.getString("gridNo"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupName, userJsonObj.getString("groupName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.headUrl, userJsonObj.getString("headUrl"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.roleName, userJsonObj.getString("roleName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.state, userJsonObj.getString("state"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.manager, true);

                                SPUtils.put(HhApplication.getInstance(), SPValue.login, true);

                                Set<String> tagSet = new LinkedHashSet<>();
                                tagSet.add(userJsonObj.getString("id"));
                                tagSet.add(userJsonObj.getString("gridNo"));
                                tagSet.add("cy_" + userJsonObj.getString("groupId"));
                                tagSet.add("cy_logout_" + CommonData.sessionKey);
                                tagSet.add("cy_" + CommonData.sessionKey);
                                tagSet.add("debug20240304");
                                XGPushManager.setTags(context, "setTag", tagSet, new XGIOperateCallback() {
                                    @Override
                                    public void onSuccess(Object o, int i) {
                                        HhLog.e("tag成功 " + i);
                                    }

                                    @Override
                                    public void onFail(Object o, int i, String s) {
                                        HhLog.e("tag失败" + s);
                                    }
                                });
                                getUserPermission();
                                HhLog.e("tagSet.toString() " + tagSet.toString());
                                /*msg.setValue("登录成功");
                                getUserPermission();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setValue(new LoadingEvent(false, ""));
                                        context.startActivity(new Intent(context, MainActivity.class));

                                        String phone = (String) SPUtils.get(HhApplication.getInstance(), SPValue.phone, "");
//                                        phone = "yunwei18";//TODO 测试环境test
                                        HhLog.e("nuLogin " + phone + "," + URLConstant.NU_SDK_PASSWORD);
                                        //NuSDK融合通信登录
                                        Api.getApiAuth().saveServerUrlData(URLConstant.NU_SDK_IP, URLConstant.NU_SDK_PORT, "", "", false);
                                        Api.getApiAuth().login(phone, URLConstant.NU_SDK_PASSWORD, Api.getApiAuth().getImeiNum(phone),
                                                true, onLoginResultListener);
                                    }
                                }, 500);*/
                            }
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


    private void getUserPermission() {
        loading.setValue(new LoadingEvent(true, "正在获取权限.."));
        //获取主菜单权限
        HhHttp.get()
                .url(URLConstant.PERMISSION_MAIN)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: PERMISSION_MAIN = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject obj = (JSONObject) data.get(0);
                            JSONArray dataList = obj.getJSONArray("menuDTOS");
                            CommonData.hasMainMap = false;
                            CommonData.hasMainVideo = false;
                            CommonData.hasMainApp = false;
                            CommonData.hasMainMy = false;
                            CommonData.hasMainMessage = false;
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject o = (JSONObject) dataList.get(i);
                                String menuCode = o.getString("menuCode");
                                HhLog.e("onSuccess: PERMISSION_MAIN menuCode = " + menuCode);
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MAP)) {
                                    CommonData.hasMainMap = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_VIDEO)) {
                                    CommonData.hasMainVideo = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_APP)) {
                                    CommonData.hasMainApp = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MY)) {
                                    CommonData.hasMainMy = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MESSAGE)) {
                                    CommonData.hasMainMessage = true;
                                }
                            }
                            //强制显示'我的'菜单
                            CommonData.hasMainMy = true;
                            SPUtils.put(context, SPValue.hasMainApp, CommonData.hasMainApp);
                            SPUtils.put(context, SPValue.hasMainVideo, CommonData.hasMainVideo);
                            SPUtils.put(context, SPValue.hasMainMessage, CommonData.hasMainMessage);
                            SPUtils.put(context, SPValue.hasMainMap, CommonData.hasMainMap);
                            SPUtils.put(context, SPValue.hasMainMy, CommonData.hasMainMy);
                            int appIndex = 0;
                            int videoIndex = 1;
                            int messageIndex = 2;
                            int mapIndex = 3;
                            int myIndex = 4;
                            int index = 0;
                            if(CommonData.hasMainApp){
                                appIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainVideo){
                                videoIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMessage){
                                messageIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMap){
                                mapIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMy){
                                myIndex = index;
                            }
                            SPUtils.put(context, SPValue.appIndex, appIndex);
                            SPUtils.put(context, SPValue.videoIndex, videoIndex);
                            SPUtils.put(context, SPValue.messageIndex, messageIndex);
                            SPUtils.put(context, SPValue.mapIndex, mapIndex);
                            SPUtils.put(context, SPValue.myIndex, mapIndex);


                            HhLog.e("onSuccess: PERMISSION_MAIN boolean = " + CommonData.hasMainApp + CommonData.hasMainVideo + CommonData.hasMainMessage + CommonData.hasMainMap + CommonData.hasMainMap);
                            HhLog.e("onSuccess: PERMISSION_MAIN index = " + appIndex + videoIndex + messageIndex + mapIndex + myIndex);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        HhLog.e("onFailure: PERMISSION_MAIN");
                    }
                });


        permissionStr = "";
        String[] menuCodeList = {CommonPermission.MAIN_APP, CommonPermission.MAIN_VIDEO, CommonPermission.MAIN_MESSAGE, CommonPermission.MAIN_MAP, CommonPermission.MAIN_MY};
        for (int i = 0; i < menuCodeList.length; i++) {
            getInnerPermission(menuCodeList[i]);
        }
    }

    private String permissionStr = "";
    private int permissionCount = 0;

    private void getInnerPermission(String code) {
        //获取子菜单权限
        HhHttp.get()
                .url(URLConstant.PERMISSION_PER)
                .addParams("menuCode", code)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: PERMISSION_PER = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray dataList = object.getJSONArray("data");
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject o = (JSONObject) dataList.get(i);
                                String code = o.getString("elementCode");
                                String p = code + "_";//分割符_
                                if (permissionStr != null && !permissionStr.isEmpty()) {
                                    permissionStr = permissionStr + "," + p;
                                } else {
                                    permissionStr = p;
                                }
                            }
                            permissionCount++;
                            if (permissionCount == 5) {
                                try {
                                    SPUtils.put(context, SPValue.permission, permissionStr);
                                    String mt = (String) SPUtils.get(context, SPValue.permission, "");
                                    HhLog.e("读取权限 PERMISSION_ " + CommonData.hasMainApp + CommonData.hasMainVideo + CommonData.hasMainMessage + CommonData.hasMainMap + CommonData.hasMainMy + mt);

                                    msg.setValue("登录成功");
                                    //getUserPermission();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setValue(new LoadingEvent(false, ""));
                                            context.startActivity(new Intent(context, MainActivity.class));

                                            String phone = (String) SPUtils.get(HhApplication.getInstance(), SPValue.phone, "");
                                            //phone = "yunwei18";//TODO 测试环境test
                                            HhLog.e("nuLogin " + phone + "," + URLConstant.NU_SDK_PASSWORD);
                                            //NuSDK融合通信登录
                                            Api.getApiAuth().saveServerUrlData(URLConstant.NU_SDK_IP, URLConstant.NU_SDK_PORT, "", "", false);
                                            Api.getApiAuth().login(phone, URLConstant.NU_SDK_PASSWORD, Api.getApiAuth().getImeiNum(phone),
                                                    true, onLoginResultListener);
                                        }
                                    }, 500);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "权限获取异常", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "权限获取异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        HhLog.e("onFailure: PERMISSION_PER");
                    }
                });
    }

}
