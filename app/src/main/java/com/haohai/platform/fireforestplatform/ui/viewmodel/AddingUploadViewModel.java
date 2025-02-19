package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.AddingDangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.AddingUploadActivity;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFile;
import com.haohai.platform.fireforestplatform.ui.bean.Picture;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.bean.TypeTree;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.Upload;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.UriTofilePath;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

public class AddingUploadViewModel extends BaseViewModel {
    public Context context;
    public List<News> dataList = new ArrayList<>();
    public StringBuffer date = new StringBuffer();//"2023-12-01"
    public int year = 2023;
    public int month = 12;
    public int day = 1;
    public String typeIds;
    public int chooseHour = 12;
    public int chooseMinute = 30;
    public int hourIndex = 0;
    public int minuteIndex = 0;
    public String hourStr = "00";
    public String minuteStr = "00";
    public List<String> hourList = new ArrayList();
    public List<String> minuteList = new ArrayList();
    public Upload upload = new Upload(new Upload.EmergIncidentVersionBean());
    public int typeIndex = 0;//1级2级3级
    public TypeTree typeBean;
    public TypeTree typeBeanSecond;
    public TypeTree typeBeanThird;
    public List<TypeTree> typeTrees = new ArrayList<>();
    public final MutableLiveData<List<TypeTree>> trees = new MutableLiveData<>();
    public List<TypeTree> typeTreesSecond = new ArrayList<>();
    public List<TypeTree> typeTreesThird = new ArrayList<>();
    public List<TypeBean> typeList = new ArrayList<>();
    public List<TypeBean> levelList = new ArrayList<>();
    public List<TypeBean> sensitiveList = new ArrayList<>();
    public List<TypeBean> areaList = new ArrayList<>();
    public TypeBean levelBean;
    public TypeBean areaBean;
    public TypeBean sensitiveBean;
    public String longitude = "";
    public String latitude = "";
    public String cityAddress = "";
    public String currentCity = "";
    public String currentQu = "";
    public int maxPicture = 5;
    public List<Picture> pictureList = new ArrayList<>();
    public List<Picture> picturePostList = new ArrayList<>();
    public List<EmergencyFile> fileList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((AddingUploadActivity)context).finish();
    }

    public void postTypeTree(){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_TYPE_TREE)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_TYPE_TREE " + response);
                        loading.postValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            typeTrees = new Gson().fromJson(String.valueOf(data), new TypeToken<List<TypeTree>>() {
                            }.getType());
                            trees.postValue(typeTrees);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.postValue(new LoadingEvent(false));
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void postPicToService(Picture image, boolean edit) {
        loading.setValue(new LoadingEvent(true, "正在提交.."));
        /*PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.POST_PICTURE);
        Uri uri = image.getUri();
        int degree = ImageUtils.readPictureDegree(uri.toString());
        Bitmap photo = null;
        try {
            photo = ImageUtils.getBitmapFormUri(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap picOne = rotaingImageView(degree, photo);
        String picStr = ImageUtils.savePhoto(picOne, context.getObbDir().getAbsolutePath(), "事件上报");
        postFormBuilder.addFile("file", picStr, new File(picStr));*/
        HhLog.e("image => " + image.toString());
        Uri uri = image.getUri();
        PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.POST_PICTURE);
        File fileByUri = UriTofilePath.uriToFileApiQ(uri,context);
        String filePathByUri = fileByUri.getPath();
        postFormBuilder.addFile("file", filePathByUri,new File(filePathByUri));
        if(filePathByUri.length() > 5){
            filePathByUri = filePathByUri.substring(filePathByUri.length()-5);
        }
        String finalFilePathByUri = filePathByUri;

        postFormBuilder
                .build()
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String url = data.getString("url");
                                fileList.add(new EmergencyFile("事件上报"+ finalFilePathByUri,"事件上报"+ finalFilePathByUri,url));

                                if(fileList.size() == picturePostList.size()){
                                    upload.setFileList(fileList);
                                    commit(edit);
                                }
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

    public void commit(boolean edit) {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        if(!edit){
            upload.setId(null);
        }
        HhHttp.postString()
                .url(edit?URLConstant.POST_EVENT_UPLOAD_EDIT:URLConstant.POST_EVENT_UPLOAD_ADD)
                .content(new Gson().toJson(upload))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            HhLog.e("POST_EVENT_UPLOAD_ADD " + edit);
                            HhLog.e("POST_EVENT_UPLOAD_ADD " + new Gson().toJson(upload));
                            HhLog.e("POST_EVENT_UPLOAD_ADD " + response);
                            if(response.contains("200")){
                                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                                ((AddingUploadActivity)context).finish();
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
