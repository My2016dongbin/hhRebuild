package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.bean.request.LoginBean;
import com.haohai.platform.fireforestplatform.utils.Common;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;

public class LoginViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void getName() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.get()
                .url(URLConstant.GET_NAME)
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: GET_NAME = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length()>0) {
                                JSONObject model = (JSONObject) data.get(0);
                                String loginSystemName = model.getString("loginSystemName");
                                if(!loginSystemName.isEmpty()){
                                    name.postValue(loginSystemName);
                                }
                            }
                            name.postValue(jsonObject.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());//400 密码错误  401 账号未注册
                    }
                });
    }

    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "登录中.."));
        String content = new Gson().toJson(new LoginBean(null, CommonUtil.getMD5(password), userName));
        HhHttp.postStringNotToken()
                .url(URLConstant.POST_LOGIN)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: URLConstant.POST_LOGIN = " + URLConstant.POST_LOGIN);
                        HhLog.e("onSuccess: content = " + content);
                        HhLog.e("onSuccess: login = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if("200".equals(code)){
                                JSONObject data = jsonObject.getJSONObject("data");
                                CommonData.token = data.getString("access_token");
                                SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                                SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                                SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                                getUserInfo();
                            }else{
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                loading.setValue(new LoadingEvent(false, ""));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.setValue(new LoadingEvent(false, ""));
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
        /*RequestParams params = new RequestParams(URLConstant.POST_LOGIN);
        params.setBodyContent(content);
        params.setAsJsonContent(true);
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String response) {
                HhLog.e("onSuccess: URLConstant.POST_LOGIN = " + URLConstant.POST_LOGIN);
                HhLog.e("onSuccess: content = " + content);
                HhLog.e("onSuccess: login = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if("200".equals(code)){
                        JSONObject data = jsonObject.getJSONObject("data");
                        CommonData.token = data.getString("access_token");
                        SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                        SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                        SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                        getUserInfo();
                    }else{
                        String msg = jsonObject.getString("msg");
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loading.setValue(new LoadingEvent(false, ""));
                }
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
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

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });*/
    }

    private void getUserInfo() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.get()
                .url(URLConstant.GET_USER_INFO)
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: URLConstant.GET_USER_INFO = " + URLConstant.GET_USER_INFO);
                        Log.e("TAG", "onSuccess: userInfo = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONObject user = jsonObject.getJSONObject("user");
                                SPUtils.put(HhApplication.getInstance(), SPValue.id, user.getString("userId"));
//                                SPUtils.put(HhApplication.getInstance(), SPValue.userCode, user.getString("userCode"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.fullName, user.getString("userName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.phone, user.getString("phonenumber"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.email, user.getString("email"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupId, user.getString("groupNo"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.gridNo, user.getString("gridNo"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupName, user.getString("groupName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.headUrl, user.getString("avatar"));
                                JSONArray roles = user.getJSONArray("roles");
                                StringBuilder role = new StringBuilder();
                                if(roles.length()>0){
                                    for (int i = 0; i < roles.length(); i++) {
                                        JSONObject obj = (JSONObject) roles.get(i);
                                        String roleName = obj.getString("roleName");
                                        if(role.toString().contains(roleName)){
                                            continue;
                                        }
                                        if(i > 0){
                                            role.append(" ");
                                        }
                                        role.append(roleName);
                                    }
                                    SPUtils.put(HhApplication.getInstance(), SPValue.roleName, role.toString());
                                }
                                SPUtils.put(HhApplication.getInstance(), SPValue.state, user.getString("status"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.manager, true);

                                SPUtils.put(HhApplication.getInstance(), SPValue.login, true);

                                Set<String> tagSet = new LinkedHashSet<>();
                                tagSet.add("sdc_cloud" + user.getString("userId"));
                                tagSet.add(user.getString("userId"));
                                tagSet.add(user.getString("gridNo"));
                                tagSet.add("sbyj_" + user.getString("groupNo"));
                                tagSet.add("debug_20240327");
                                XGPushManager.setTags(context, "setTag", tagSet);


                                /*getUserPermission();*/

                                msg.setValue("登录成功");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setValue(new LoadingEvent(false, ""));
                                        context.startActivity(new Intent(context, MainActivity.class));
                                        ((LoginActivity)context).finish();
                                    }
                                }, 500);

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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                    }
                });


        permissionStr = "";
        String[] menuCodeList = {CommonPermission.MAIN_APP,CommonPermission.MAIN_VIDEO,CommonPermission.MAIN_MESSAGE,CommonPermission.MAIN_MAP,CommonPermission.MAIN_MY};
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
                                    HhLog.e("读取权限 PERMISSION_ " + CommonData.hasMainApp + CommonData.hasMainVideo + CommonData.hasMainMessage + CommonData.hasMainMap + CommonData.hasMainMy  + mt);

                                    msg.setValue("登录成功");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setValue(new LoadingEvent(false, ""));
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            ((LoginActivity) context).finish();
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
                    }
                });
    }

}
