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
import com.zhy.http.okhttp.request.GetRequest;
import com.zhy.http.okhttp.request.OtherRequest;
import com.zhy.http.okhttp.request.PostFileRequest;
import com.zhy.http.okhttp.request.PostFormRequest;
import com.zhy.http.okhttp.request.PostStringRequest;
import com.zhy.http.okhttp.request.RequestCall;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        PostFormBuilder postFormBuilder = new LoginCheckedPostFormBuilder();
        postFormBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postFormBuilder.addHeader("NetworkType", "Internet");

        return postFormBuilder;
    }

    public static GetBuilder get() {
        GetBuilder getBuilder = new LoginCheckedGetBuilder();
        getBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        getBuilder.addHeader("NetworkType", "Internet");
        //getBuilder.addHeader("NetworkType", "Internet");

        return getBuilder;
    }
    public static GetBuilder getLogin() {
        GetBuilder getBuilder = new LoginCheckedGetBuilder();
        //getBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        getBuilder.addHeader("NetworkType", "Internet");
        //getBuilder.addHeader("NetworkType", "Internet");

        return getBuilder;
    }
    public static OtherRequestBuilder put() {
        OtherRequestBuilder builder = new LoginCheckedOtherRequestBuilder(OkHttpUtils.METHOD.PUT);
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        builder.addHeader("NetworkType", "Internet");
        //builder.mediaType(MediaType.parse("application/json; charset=utf-8"));

        return builder;
    }

    public static PostStringBuilder postString() {
        PostStringBuilder postStringBuilder = new LoginCheckedPostStringBuilder();
        postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        postStringBuilder.addHeader("NetworkType", "Internet");

        return postStringBuilder;
    }

    public static PostStringBuilder postStringTrackUpload() {
        PostStringBuilder postStringBuilder = new LoginCheckedPostStringBuilder();
        postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        postStringBuilder.addHeader("NetworkType", "Internet");

        return postStringBuilder;
    }

    public static PostFileBuilder postFile() {
        PostFileBuilder postFileBuilder = new LoginCheckedPostFileBuilder();
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
        okHttpClient.newCall(builder.build()).enqueue(wrapOkHttpCallback(callback));
    }

    public static void uploadFile(String url, File file, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(wrapOkHttpCallback(callback));
    }
    public static void postX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().post(params, wrapXCallback(callback));
    }
    public static void getX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().get(params, wrapXCallback(callback));
    }
    public static void methodX(HttpMethod method, RequestParams params, org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().request(method,params, wrapXCallback(callback));
    }

    private static class LoginCheckedGetBuilder extends GetBuilder {
        @Override
        public RequestCall build() {
            if (params != null) {
                url = appendParams(url, params);
            }

            return new LoginCheckedRequestCall(new GetRequest(url, tag, params, headers, id));
        }
    }

    private static class LoginCheckedPostFormBuilder extends PostFormBuilder {
        private final List<FileInput> files = new ArrayList<>();

        @Override
        public PostFormBuilder files(String key, Map<String, File> files) {
            for (String filename : files.keySet()) {
                this.files.add(new FileInput(key, filename, files.get(filename)));
            }
            return this;
        }

        @Override
        public PostFormBuilder addFile(String name, String filename, File file) {
            files.add(new FileInput(name, filename, file));
            return this;
        }

        @Override
        public PostFormBuilder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        @Override
        public PostFormBuilder addParams(String key, String val) {
            if (this.params == null) {
                params = new LinkedHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        @Override
        public RequestCall build() {
            return new LoginCheckedRequestCall(new PostFormRequest(url, tag, params, headers, files, id));
        }
    }

    private static class LoginCheckedOtherRequestBuilder extends OtherRequestBuilder {
        private RequestBody requestBody;
        private final String method;
        private String content;

        public LoginCheckedOtherRequestBuilder(String method) {
            super(method);
            this.method = method;
        }

        @Override
        public OtherRequestBuilder requestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        @Override
        public OtherRequestBuilder requestBody(String content) {
            this.content = content;
            return this;
        }

        @Override
        public RequestCall build() {
            return new LoginCheckedRequestCall(new OtherRequest(requestBody, content, method, url, tag, params, headers, id));
        }
    }

    private static class LoginCheckedPostFileBuilder extends PostFileBuilder {
        private File file;
        private MediaType mediaType;

        @Override
        public PostFileBuilder file(File file) {
            this.file = file;
            return this;
        }

        @Override
        public PostFileBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        @Override
        public RequestCall build() {
            return new LoginCheckedRequestCall(new PostFileRequest(url, tag, params, headers, file, mediaType, id));
        }
    }

    private static class LoginCheckedPostStringBuilder extends PostStringBuilder {
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
            return new LoginCheckedRequestCall(new PostStringRequest(url, tag, params, headers, content, mediaType, id));
        }
    }

    private static class LoginCheckedRequestCall extends RequestCall {
        public LoginCheckedRequestCall(com.zhy.http.okhttp.request.OkHttpRequest request) {
            super(request);
        }

        @Override
        public void execute(com.zhy.http.okhttp.callback.Callback callback) {
            super.execute(wrapLoginCheckedCallback(callback));
        }
    }

    private static com.zhy.http.okhttp.callback.Callback wrapLoginCheckedCallback(final com.zhy.http.okhttp.callback.Callback callback) {
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
                handleUnauthorized(response);
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

    private static Callback wrapOkHttpCallback(final Callback callback) {
        if (callback == null) {
            return null;
        }

        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleUnauthorized(response);
                callback.onResponse(call, response);
            }
        };
    }

    private static org.xutils.common.Callback.CommonCallback<String> wrapXCallback(final org.xutils.common.Callback.CommonCallback<String> callback) {
        if (callback == null) {
            return null;
        }

        return new XLoginCheckedCallback(callback);
    }

    private static class XLoginCheckedCallback implements org.xutils.common.Callback.CommonCallback<String> {
        private final org.xutils.common.Callback.CommonCallback<String> callback;

        XLoginCheckedCallback(org.xutils.common.Callback.CommonCallback<String> callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(String result) {
            callback.onSuccess(result);
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            handleUnauthorized(ex);
            callback.onError(ex, isOnCallback);
        }

        @Override
        public void onCancelled(org.xutils.common.Callback.CancelledException cex) {
            callback.onCancelled(cex);
        }

        @Override
        public void onFinished() {
            callback.onFinished();
        }
    }

    private static void handleUnauthorized(Response response) {
        if (response != null && response.code() == 401 && isAuthorizedRequest(response)) {
            showNoPermissionToast();
        }
    }

    private static boolean isAuthorizedRequest(Response response) {
        Request request = response.request();
        return request != null && request.header("Authorization") != null;
    }

    private static void handleUnauthorized(Throwable ex) {
        if (ex instanceof HttpException && ((HttpException) ex).getCode() == 401) {
            showNoPermissionToast();
        }
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
