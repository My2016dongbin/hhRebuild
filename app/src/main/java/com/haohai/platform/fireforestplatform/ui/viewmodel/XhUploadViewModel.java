package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.XhUploadActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Area;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

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

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

public class XhUploadViewModel extends BaseViewModel {
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
    public boolean fromMap = false;
    public StringBuffer date = new StringBuffer();
    public StringBuffer endDate = new StringBuffer();
    public int year;
    public int month;
    public int day;
    public int chooseHour;
    public int chooseMinute;
    public boolean isChooseStarTime;

    public String fireName;
    public String fireAddress;
    public String fireLng;
    public String fireLat;
    public String fireHb;
    public String fireDate;
    public String fireMa;
    public String fireNiu;
    public String fireYang;
    public List<String> copyImageStrList;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((XhUploadActivity)context).finish();
    }

    public void postPicToService() {
        HhLog.e("size" + list.size());
        HhLog.e("list" + list.toString());
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", fireAddress);
            jsonObject.put("alarmLongitude", fireLng);
            jsonObject.put("alarmLatitude", fireLat);
            jsonObject.put("alarmAltitude", fireHb);
            jsonObject.put("alarmDatetime", fireDate);
            jsonObject.put("name", fireName);
            jsonObject.put("fireNo", " ");
            jsonObject.put("status", 0);
            jsonObject.put("horseCount", fireMa);
            jsonObject.put("cowCount", fireNiu);
            jsonObject.put("sheepCount", fireYang);
            if (copyImageStrList.size() > 0) {
                jsonObject.put("picPath1", copyImageStrList.get(0));
                if (copyImageStrList.size() > 1) {
                    jsonObject.put("picPath2", copyImageStrList.get(1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HhLog.e("postDataToService: " + jsonObject.toString());
        HhLog.e("onSuccess: URLConstant.POST_XH_UPLOAD " + URLConstant.POST_XH_UPLOAD);
        HhHttp.postString()
                .url(URLConstant.POST_XH_UPLOAD)
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
                        ((XhUploadActivity)context).finish();
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
