package com.haohai.platform.fireforestplatform.constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.event.Ext;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.request.PostStringRequest;
import com.zhy.http.okhttp.request.RequestCall;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qc
 * on 2023/1/30.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class HhHttp {
    public static PostFormBuilder post() {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postFormBuilder.addHeader("NetworkType", "Internet");

        return postFormBuilder;
    }

    public static GetBuilder get() {
        GetBuilder getBuilder = OkHttpUtils.get();
        getBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        getBuilder.addHeader("NetworkType", "Internet");
        //getBuilder.addHeader("NetworkType", "Internet");

        return getBuilder;
    }
    public static GetBuilder getLogin() {
        GetBuilder getBuilder = OkHttpUtils.get();
        //getBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        getBuilder.addHeader("NetworkType", "Internet");
        //getBuilder.addHeader("NetworkType", "Internet");

        return getBuilder;
    }
    public static OtherRequestBuilder put() {
        OtherRequestBuilder builder = OkHttpUtils.put();
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        builder.addHeader("NetworkType", "Internet");
        //builder.mediaType(MediaType.parse("application/json; charset=utf-8"));

        return builder;
    }

    public static PostStringBuilder postString() {
        PostStringBuilder postStringBuilder = OkHttpUtils.postString();
        postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        postStringBuilder.addHeader("NetworkType", "Internet");

        return postStringBuilder;
    }

    public static PostStringBuilder postStringTrackUpload() {
        PostStringBuilder postStringBuilder = new TrackUploadPostStringBuilder();
        postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        postStringBuilder.addHeader("NetworkType", "Internet");

        return postStringBuilder;
    }

    public static PostFileBuilder postFile() {
        PostFileBuilder postFileBuilder = OkHttpUtils.postFile();
        postFileBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postFileBuilder.addHeader("NetworkType", "Internet");
        postFileBuilder.mediaType(MediaType.parse("multipart/form-data"));

        return postFileBuilder;
    }

    public static void method(String method,String url, String content, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.method(method, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content));
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        builder.addHeader("NetworkType", "Internet");
        okHttpClient.newCall(builder.build()).enqueue(callback);
    }

    public static void uploadFile(String url, File file, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
    public static void postX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().post(params, callback);
    }
    public static void getX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().get(params, callback);
    }
    public static void methodX(HttpMethod method, RequestParams params, org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().request(method,params, callback);
    }

    private static class TrackUploadPostStringBuilder extends PostStringBuilder {
        private String content;
        private MediaType mediaType;

        @Override
        public PostStringBuilder content(String content) {
            this.content = content;
            return this;
        }

        @Override
        public PostStringBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        @Override
        public RequestCall build() {
            return new TrackUploadRequestCall(new PostStringRequest(url, tag, params, headers, content, mediaType, id));
        }
    }

    private static class TrackUploadRequestCall extends RequestCall {
        public TrackUploadRequestCall(PostStringRequest request) {
            super(request);
        }

        @Override
        public void execute(com.zhy.http.okhttp.callback.Callback callback) {
            super.execute(wrapTrackUploadCallback(callback));
        }
    }

    private static com.zhy.http.okhttp.callback.Callback wrapTrackUploadCallback(final com.zhy.http.okhttp.callback.Callback callback) {
        if (callback == null) {
            return null;
        }

        return new com.zhy.http.okhttp.callback.Callback() {
            @Override
            public void onBefore(Request request, int id) {
                callback.onBefore(request, id);
            }

            @Override
            public void onAfter(int id) {
                callback.onAfter(id);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                callback.inProgress(progress, total, id);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (response.code() == 401) {
                    showNoPermissionToast();
                }
                return callback.validateReponse(response, id);
            }

            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {
                return callback.parseNetworkResponse(response, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                callback.onError(call, e, id);
            }

            @Override
            public void onResponse(Object response, int id) {
                callback.onResponse(response, id);
            }
        };
    }

    private static void showNoPermissionToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                outLogin();
            }
        });
    }

    public static void outLogin(){
        Toast.makeText(HhApplication.getInstance(), "登录信息失效，请重新登录", Toast.LENGTH_LONG).show();
        SPUtils.put(HhApplication.getInstance(),SPValue.login,false);
        CommonData.token = "";
        CommonData.videoAddingIndex = 0;
        CommonData.videoDeleteIndex = 0;
        CommonData.videoDeleteMonitorId = "";
        CommonData.videoDeleteChannelId = "";
        CommonData.videoPlayingIndexList = new ArrayList<>();
        CommonData.videoDeleteModelList = new ArrayList<>();
        CommonData.mainTabIndex = 0;
        CommonData.walkDistance = 0;
        SPUtils.put(HhApplication.getInstance(),SPValue.token,"");
        Intent intent = new Intent(HhApplication.getInstance(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        HhApplication.getInstance().startActivity(intent);
        EventBus.getDefault().post(new Ext());
    }
}
