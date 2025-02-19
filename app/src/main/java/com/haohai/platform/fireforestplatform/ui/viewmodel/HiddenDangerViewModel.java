package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.FireUploadActivity;
import com.haohai.platform.fireforestplatform.ui.activity.HiddenDangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LaunchActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Area;
import com.haohai.platform.fireforestplatform.ui.bean.LeiBie;
import com.haohai.platform.fireforestplatform.ui.bean.Leixing;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

public class HiddenDangerViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();
    public List<ChooseImage> list = new ArrayList<>();
    public String longitude;
    public String latitude;
    public String cityAddress;
    public String currentCity;
    public List<Area> allAreaList = new ArrayList<>();
    public WheelView areaWy;
    public int shengSelectIndex = 0;
    public int shiSelectIndex = 0;
    public boolean isChooseSheng = false;
    public String currentChooseSheng = "";
    public String currentChooseShi = "";
    public boolean fromMap = false;
    public List<Area> shengList = new ArrayList<>();
    public List<String> shengStrList = new ArrayList<>();
    public List<Area> shiList = new ArrayList<>();
    public List<String> shiStrList = new ArrayList<>();
    public int currentChooseArea = 0;  //当前在选择省还是市   0选择省  1选择市
    public int currentLeibie = 0;   //0 是 1否
    public int leibieSelectIndex = 0;
    public int leixingSelectIndex = 0;
    public WheelView typeWy;
    public String currentChooseLeibie = "";
    public String currentChooseLeixing = "";
    public List<LeiBie> leiBieList = new ArrayList<>();
    public StringBuffer date = new StringBuffer();
    public StringBuffer endDate = new StringBuffer();
    public int year;
    public int month;
    public int day;
    public int chooseHour;
    public int chooseMinute;
    public boolean isChooseStarTime;
    public List<String> imgStrList = new ArrayList<>();
    public String shengStr;
    public String shiStr;
    public String resourceName;
    public String address;
    public String dangerDescription;
    public String remark;
    public double lat;
    public double lng;

    public void start(Context context) {
        this.context = context;
        initLeiBieData();
    }


    public void barLeftClick(View v) {
        ((HiddenDangerActivity) context).finish();
    }

    public void initLeiBieData() {
        List<Leixing> leixings = new ArrayList<>();
        leixings.add(new Leixing(11, "火种"));
        leixings.add(new Leixing(12, "可燃物"));
        leiBieList.add(new LeiBie("火源管控", leixings));

        List<Leixing> leixings1 = new ArrayList<>();
        leixings1.add(new Leixing(21, "水罐"));
        leixings1.add(new Leixing(22, "灭火机"));
        leixings1.add(new Leixing(23, "水泵"));
        leiBieList.add(new LeiBie("灭火设施", leixings1));

        List<Leixing> leixings2 = new ArrayList<>();
        leixings2.add(new Leixing(31, "防火车辆"));
        leixings2.add(new Leixing(32, "通信器材"));
        leixings2.add(new Leixing(33, "个人装备"));
        leiBieList.add(new LeiBie("物资储备", leixings2));

        List<Leixing> leixings3 = new ArrayList<>();
        leixings3.add(new Leixing(41, "应急方案"));
        leixings3.add(new Leixing(42, "值班备勤"));
        leixings3.add(new Leixing(43, "宣传教育"));
        leiBieList.add(new LeiBie("日常管理", leixings3));
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

    public void postPicToService() {
        HhLog.e("size" + list.size());
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.POST_PICTURE);
        try{
            for (int i = 0; i < list.size(); i++) {
                Uri uri = list.get(i).getUri();
                int degree = ImageUtils.readPictureDegree(uri.toString());
                Bitmap photo = ImageUtils.getBitmapFormUri(context, uri);

                Bitmap picOne = rotaingImageView(degree, photo);
                String picStr = ImageUtils.savePhoto(picOne, context.getObbDir().getAbsolutePath(), "fileName" + i);
                postFormBuilder.addFile("file",  picStr,new File(picStr));
            }
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        postFormBuilder
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
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
        String shengId = "";
        for (int i = 0; i < shengList.size(); i++) {
            if (shengList.get(i).getName().equals(shengStr)) {
                shengId = shengList.get(i).getId();
            }
        }
        String shiId = "";
        for (int i = 0; i < shiList.size(); i++) {
            if (shiList.get(i).getName().equals(shiStr)) {
                shiId = shiList.get(i).getId();
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cityCode", shiId);
            jsonObject.put("cityName", shiStr);
            jsonObject.put("provinceCode", shengId);
            jsonObject.put("provinceName", shengStr);
            jsonObject.put("resourceName", resourceName);
            jsonObject.put("address", address);
            jsonObject.put("dangerType", currentChooseLeixing);          //隐患类型
            jsonObject.put("dangerDescription", dangerDescription);  //隐患描述
            jsonObject.put("remark", remark);  //整治描述
            JSONObject position = new JSONObject();
            position.put("lat", lat);
            position.put("lng", lng);
            jsonObject.put("position", position); // 整治描述 ,
            for (int i = 0; i < imgStrList.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgStrList.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(URLConstant.POST_HIDDEN_DANGER);
        params.setBodyContent(jsonObject.toString());
        HhLog.e("postPicToService: " + params);
        HhLog.e("postPicToService: " + jsonObject.toString());
        HhHttp.postString()
                .url(URLConstant.POST_HIDDEN_DANGER)
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
                        ((HiddenDangerActivity)context).finish();
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
