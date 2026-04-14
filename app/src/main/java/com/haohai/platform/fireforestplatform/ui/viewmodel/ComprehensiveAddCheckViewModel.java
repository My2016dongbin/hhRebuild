package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

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
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveAddCheckActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CheckImage;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.ResInfo;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;

public class ComprehensiveAddCheckViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public String id;
    public boolean pass = true;
    public final int imageSize = 5;
    public final MutableLiveData<List<CheckImage>> imageList = new MutableLiveData<>(new ArrayList<>());
    public void start(Context context){
        this.context = context;
    }
    public List<Res> resList = new ArrayList<>();
    public List<ResInfo> resInfoList = new ArrayList<>();
    public int resIndex = 0;
    public int resInfoIndex = 0;
    public String apiCode = "";
    public String apiCodeString = "";


    public void barLeftClick(View v){
        ((ComprehensiveAddCheckActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isDisplay", "1");
        } catch (JSONException e) {
        }
        HhHttp.postString()
                .url(URLConstant.POST_RES_TYPE_LIST)
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_RES_TYPE_LIST " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            resList =  new Gson().fromJson(String.valueOf(data), new TypeToken<List<Res>>() {
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

    public void getRes(String code) {
        apiCode = code;
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.postString()
                .url(URLConstant.GET_RES_LIST + code + "/list")
                .content(new JSONObject().toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        //HhLog.e("GET_RES_LIST " + URLConstant.GET_RES_LIST + code + "/list");
                        //HhLog.e("GET_RES_LIST " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            resInfoList =  new Gson().fromJson(String.valueOf(data), new TypeToken<List<ResInfo>>() {
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


    public void getGridResource(String resCode) {
        loading.setValue(new LoadingEvent(true,"加载中.."));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("updateTime", "2010-11-21T08:36:31.420Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/resourceList/getResourcesByGrid?districtNo=371300");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + CommonData.token);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("getGridResource " + params);
                HhLog.e("getGridResource " + result);
                try {
                    JSONObject obj = new JSONObject(result);
                    String code = obj.getString("code");
                    if (code.equals("200")){
                        JSONArray data = obj.getJSONArray("data");
                        if(data.length()>0){
                            JSONObject model = (JSONObject) data.get(0);
                            if("waterSource".equals(resCode)){
                                resInfoList = new ArrayList<>();
                                JSONArray array1 = model.getJSONArray("waterBag"+"List");
                                resInfoList.addAll(new Gson().fromJson(String.valueOf(array1), new TypeToken<List<ResInfo>>() {
                                }.getType()));
                                JSONArray array2 = model.getJSONArray("waterReservoir"+"List");
                                resInfoList.addAll(new Gson().fromJson(String.valueOf(array2), new TypeToken<List<ResInfo>>() {
                                }.getType()));
                                JSONArray array3 = model.getJSONArray("reservoir"+"List");
                                resInfoList.addAll(new Gson().fromJson(String.valueOf(array3), new TypeToken<List<ResInfo>>() {
                                }.getType()));
                            }
                            JSONArray modelJSONArray = model.getJSONArray(resCode+"List");
                            resInfoList =  new Gson().fromJson(String.valueOf(modelJSONArray), new TypeToken<List<ResInfo>>() {
                            }.getType());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e( "onError: materialRepository请求失败 getGrid " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loading.setValue(new LoadingEvent(false));
            }
        });
    }

}
