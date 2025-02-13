package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.HiddenDangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LiveUploadActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class LiveUploadViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();
    public List<ChooseImage> list = new ArrayList<>();
    public String videoPath = "";
    public String videoStr = "";
    public String imageStr = "";
    public String taskId = "";
    public String liveState = "";
    public String otherInfo = "";
    public List<String> imgStrList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((LiveUploadActivity)context).finish();
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
            if(videoPath!=null&&!videoPath.isEmpty()){
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
                        HhLog.e(response);
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

                                List<String> copyList = new ArrayList<>(imgStrList);
                                if(videoPath!=null&&!videoPath.isEmpty()){
                                    videoStr = imgStrList.get(imgStrList.size()-1);
                                    copyList.remove(imgStrList.size()-1);
                                }

                                for (int i = 0; i < copyList.size(); i++) {
                                    if(i==0){
                                        imageStr = imageStr + copyList.get(i);
                                    }else{
                                        imageStr = imageStr + "," + copyList.get(i);
                                    }
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
    public void postPicToServiceX() {
        RequestParams params = new RequestParams(URLConstant.POST_PICTURE);
        for (int i = 0; i < list.size(); i++) {
            Uri uri = list.get(i).getUri();
            int degree = ImageUtils.readPictureDegree(uri.toString());
            try {
                Bitmap photo = ImageUtils.getBitmapFormUri(context, uri);
                Bitmap picOne = rotaingImageView(degree, photo);
                String picStr = ImageUtils.savePhoto(picOne, context.getObbDir().getAbsolutePath(), "fileName" + i);
                params.addBodyParameter("file", new File(picStr),null,picStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        params.addBodyParameter("file", new File(videoPath),null,videoPath);
        params.setAsJsonContent(true);
        params.setMultipart(true);
        params.setConnectTimeout(60000);
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String response) {
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

                        List<String> copyList = new ArrayList<>(imgStrList);
                        if(videoPath!=null&&!videoPath.isEmpty()){
                            videoStr = imgStrList.get(imgStrList.size()-1);
                            copyList.remove(imgStrList.size()-1);
                        }

                        for (int i = 0; i < copyList.size(); i++) {
                            if(i==0){
                                imageStr = imageStr + copyList.get(i);
                            }else{
                                imageStr = imageStr + "," + copyList.get(i);
                            }
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
            public void onError(Throwable e, boolean isOnCallback) {
                HhLog.e("onFailure: " + e.toString());
                msg.setValue(e.getMessage());
                loading.setValue(new LoadingEvent(false));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void postDataToService() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteConditions", liveState);
            jsonObject.put("otherConditions", otherInfo);
            jsonObject.put("groupId", SPUtils.get(context, SPValue.groupId,""));
            jsonObject.put("taskId",taskId);
            jsonObject.put("videoUrl",videoStr);
            jsonObject.put("imgUrl",imageStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HhLog.e("postPicToService: " + jsonObject.toString());
        HhHttp.postString()
                .url(URLConstant.POST_LIVE_UPLOAD)
                .content(jsonObject.toString())
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                loading.setValue(new LoadingEvent(false));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("200")) {
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                        ((LiveUploadActivity)context).finish();
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
