package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.FgMainRefresh;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.FireUploadActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LaunchActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Area;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.UriTofilePath;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class FireUploadViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();
    public List<ChooseImage> list = new ArrayList<>();
    public List<String> imgStrList = new ArrayList<>();
    public String longitude;
    public String latitude;
    public String cityAddress;
    public String currentCity;
    public String currentQu;
    public List<Area> allAreaList = new ArrayList<>();
    public WheelView areaWy;
    public int shengSelectIndex = 0;
    public int shiSelectIndex = 0;
    public int quSelectIndex = 0;
    public boolean isChooseSheng = false;
    public String currentChooseSheng = "";
    public String currentChooseShi = "";
    public String currentChooseQu = "";
    public boolean fromMap = false;
    public List<Area> shengList = new ArrayList<>();
    public List<String> shengStrList = new ArrayList<>();
    public List<Area> shiList = new ArrayList<>();
    public List<String> shiStrList = new ArrayList<>();
    public List<Area> quList = new ArrayList<>();
    public List<String> quStrList = new ArrayList<>();
    public int currentChooseArea = 0;  //当前在选择省还是市   0选择省  1选择市
    public StringBuffer date = new StringBuffer();
    public StringBuffer endDate = new StringBuffer();
    public int year;
    public int month;
    public int day;
    public int chooseHour;
    public int chooseMinute;
    public boolean isChooseStarTime;
    public String shengStr;
    public String shiStr;
    public String quStr;
    public String shengId;
    public String shiId;
    public String quId;
    public String videoPath = "";
    public String videoStr = "";

    public String fireName;
    public String fireAddress;
    public String fireLng;
    public String fireLat;
    public String fireDate;
    public String fireArea;
    public String fireReporter;
    public List<String> copyImageStrList;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((FireUploadActivity)context).finish();
    }


    public void getGridData() {
        DbConfig dbConfig = new DbConfig(context);
        allAreaList = dbConfig.getGridList();
        if(allAreaList!=null){
            initArea();
        }else{
            loading.setValue(new LoadingEvent(true, "数据加载中.."));
            HhHttp.get()
                    .url(URLConstant.GET_GRID)
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(this, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            loading.setValue(new LoadingEvent(false, ""));
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray data = jsonObject.getJSONArray("data");
                                allAreaList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Area>>() {
                                }.getType());
                                //存储网格信息
                                DbManager db = dbConfig.getDbManager();
                                try {
                                    db.saveOrUpdate(allAreaList);
                                } catch (DbException e) {

                                }
                                initArea();

                            } catch (JSONException e) {
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

    public void initArea() {
        shengStrList.clear();
        shengList.clear();
        shengStrList.add("请选择省");
        for (int i = 0; i < allAreaList.size(); i++) {
            if ("1".equals(allAreaList.get(i).getAreaLevel())) {
                shengList.add(allAreaList.get(i));
                shengStrList.add(allAreaList.get(i).getName());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void postPicToService() {
        HhLog.e("size" + list.size());
        HhLog.e("list" + list.toString());
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.POST_PICTURE);
        try{
            for (int i = 0; i < list.size(); i++) {
                Uri uri = list.get(i).getUri();
                /*int degree = ImageUtils.readPictureDegree(uri.toString());
                Bitmap photo = ImageUtils.getBitmapFormUri(context, uri);

                Bitmap picOne = rotaingImageView(degree, photo);
                String picStr = ImageUtils.savePhoto(picOne, context.getObbDir().getAbsolutePath(), "fileName" + i);
                postFormBuilder.addFile("file",  picStr,new File(picStr));*/
                File fileByUri = UriTofilePath.uriToFileApiQ(uri,context);
                String filePathByUri = fileByUri.getPath();
                postFormBuilder.addFile("file", filePathByUri,new File(filePathByUri));
                if(filePathByUri.length() > 5){
                    filePathByUri = filePathByUri.substring(filePathByUri.length()-5);
                }
                String finalFilePathByUri = filePathByUri;

            }
            if(videoPath!=null && !videoPath.isEmpty()){
                postFormBuilder.addFile("file",  videoPath,new File(videoPath));
            }
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        postFormBuilder
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: post picture " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                imgStrList.clear();
                                JSONObject data = jsonObject.getJSONObject("data");
                                JSONArray imgStrArray = data.getJSONArray("img");
                                for (int i = 0; i < imgStrArray.length(); i++) {
                                    String imgStr = imgStrArray.getString(i);
                                    imgStrList.add(imgStr);
                                }

                                copyImageStrList = new ArrayList<>(imgStrList);
                                if(videoPath!=null&&!videoPath.isEmpty()){
                                    videoStr = imgStrList.get(imgStrList.size()-1);
                                    copyImageStrList.remove(imgStrList.size()-1);
                                }

                                postDataToService();
                            } else {
                                Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
                                loading.setValue(new LoadingEvent(false));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }


    private void postDataToService() {
        shengStr = currentChooseSheng;
        shiStr = currentChooseShi;
        quStr = currentChooseQu;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", fireAddress);
            jsonObject.put("cityCode", shiId);
            jsonObject.put("cityName", shiStr);
            jsonObject.put("provinceCode", shengId);
            jsonObject.put("provinceName", shengStr);

            jsonObject.put("longitude", fireLng);
            jsonObject.put("latitude", fireLat);

            jsonObject.put("discoverTime", fireDate);
            jsonObject.put("fireName", fireName);
            jsonObject.put("fireNo", " ");
            jsonObject.put("status", 0);
            jsonObject.put("countyName", quStr);
            jsonObject.put("countyCode", quId);
            jsonObject.put("fireArea", fireArea);
            jsonObject.put("videoPath1", videoStr);
            jsonObject.put("reporter", fireReporter);
            if (copyImageStrList.size() > 0) {
                jsonObject.put("picPath1", copyImageStrList.get(0));
                if (copyImageStrList.size() > 1) {
                    jsonObject.put("picPath2", copyImageStrList.get(1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(URLConstant.POST_FIRE_UPLOAD);
        params.setBodyContent(jsonObject.toString());
        HhLog.e("postDataToService: " + params);
        HhLog.e("postDataToService: " + jsonObject.toString());
        HhHttp.postString()
                .url(URLConstant.POST_FIRE_UPLOAD)
                .content(jsonObject.toString())
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                loading.setValue(new LoadingEvent(false));
                HhLog.e("onSuccess: " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("200")) {
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new FgMainRefresh());
                        ((FireUploadActivity)context).finish();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

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
