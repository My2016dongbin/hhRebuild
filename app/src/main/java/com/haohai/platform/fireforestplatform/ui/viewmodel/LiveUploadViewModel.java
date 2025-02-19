package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.HiddenDangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LiveUploadActivity;
import com.haohai.platform.fireforestplatform.ui.bean.LiveUpload;
import com.haohai.platform.fireforestplatform.ui.bean.PersonParam;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.UriTofilePath;
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
import java.util.Date;
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
    public TaskList task = new TaskList();
    public List<String> imgStrList = new ArrayList<>();
    public List<com.haohai.platform.fireforestplatform.ui.bean.File> fileList = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }


    public void barLeftClick(View v) {
        ((LiveUploadActivity) context).finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void postPicToService(ChooseImage image) {
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
        String picStr = ImageUtils.savePhoto(picOne, context.getObbDir().getAbsolutePath(), "现场上报");
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
                                fileList.add(new com.haohai.platform.fireforestplatform.ui.bean.File(taskId,"现场上报"+finalFilePathByUri,"现场上报"+finalFilePathByUri,url));

                                if(fileList.size() == list.size()){
                                    if(videoPath!=null&&!videoPath.isEmpty()){
                                        postVideoToService();
                                    }else{
                                        postDataToService();
                                    }
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
    public void postVideoToService() {
        loading.setValue(new LoadingEvent(true, "正在提交.."));
        PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.POST_PICTURE);
        postFormBuilder.addFile("file",  videoPath,new File(videoPath));

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
                                fileList.add(new com.haohai.platform.fireforestplatform.ui.bean.File(taskId,"现场上报.mp4","现场上报.mp4",url));

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
        LiveUpload liveUpload = new LiveUpload();
        liveUpload.setTaskStatus(task.getTaskStatus()==null?"wait":task.getTaskStatus());
        liveUpload.setIncidentId(task.getIncidentId());
        liveUpload.setTaskId(task.getId());
        liveUpload.setTaskEndTime(task.getTaskEndTime());
        liveUpload.setTaskStartTime(task.getTaskStartTime());
        liveUpload.setTaskName(task.getTaskName());
        liveUpload.setTaskDesc(liveState);
        List<PersonParam> personDTOList = new ArrayList<>();
        PersonParam personParam = new PersonParam(taskId, taskId, (String) SPUtils.get(context, SPValue.id, ""), (String) SPUtils.get(context, SPValue.fullName, ""));
        personParam.setGridNo((String) SPUtils.get(context, SPValue.gridNo, ""));
        personParam.setGroupNo((String) SPUtils.get(context, SPValue.groupId, ""));
        personDTOList.add(personParam);
        liveUpload.setPersonDTOList(personDTOList);
        List<com.haohai.platform.fireforestplatform.ui.bean.File> fileDTOList = new ArrayList<>();
        for (int m = 0; m < fileList.size(); m++) {
            com.haohai.platform.fireforestplatform.ui.bean.File file = fileList.get(m);
            com.haohai.platform.fireforestplatform.ui.bean.File model = new com.haohai.platform.fireforestplatform.ui.bean.File(taskId, file.getFileName(), file.getFileStorageName(), file.getFileUrl());
            model.setDelFlag("0");
            model.setId(taskId);
            model.setFileType("create");
            fileDTOList.add(model);
        }
        liveUpload.setReportFileList(fileDTOList);
        HhHttp.postString()
                .url(URLConstant.POST_LIVE_UPLOAD)
                .content(new Gson().toJson(liveUpload))
                .build().execute(new LoggedInStringCallback(this, context) {
            @Override
            public void onSuccess(String response, int id) {
                loading.setValue(new LoadingEvent(false));
                HhLog.e("POST_LIVE_UPLOAD content " + new Gson().toJson(liveUpload));
                HhLog.e("POST_LIVE_UPLOAD response " + response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("msg");
                    if (code.equals("200")) {
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                        ((LiveUploadActivity) context).finish();
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
