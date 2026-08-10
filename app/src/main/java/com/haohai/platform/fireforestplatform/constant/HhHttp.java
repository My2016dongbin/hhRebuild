package com.haohai.platform.fireforestplatform.constant;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.app.RequestTracker;
import org.xutils.http.request.UriRequest;
import org.xutils.x;
import org.xutils.ex.HttpException;

import java.io.File;
import java.io.IOException;

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
    public static final int TOKEN_FAILURE_CODE = 417;
    private static final long TOKEN_FAILURE_INTERVAL = 1500L;
    private static final String TOKEN_FAILURE_MSG = "reponse's code is : " + TOKEN_FAILURE_CODE;
    private static long tokenFailureBroadcastTime = 0L;

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
        okHttpClient.newCall(builder.build()).enqueue(getCallback(callback));
    }

    public static void uploadFile(String url, File file, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(getCallback(callback));
    }
    public static void postX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().post(params, getCallback(callback));
    }
    public static void getX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().get(params, getCallback(callback));
    }
    public static void methodX(HttpMethod method, RequestParams params, org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        params.addHeader("NetworkType", "Internet");
        params.setConnectTimeout(20000);
        x.http().request(method,params, getCallback(callback));
    }

    public static boolean isTokenFailure(Throwable e) {
        if (e instanceof HttpException) {
            return ((HttpException) e).getCode() == TOKEN_FAILURE_CODE;
        }
        String msg = e == null ? "" : e.toString();
        return msg.contains(TOKEN_FAILURE_MSG);
    }

    public static boolean isTokenFailure(Response response) {
        return response != null && response.code() == TOKEN_FAILURE_CODE;
    }

    public static void sendTokenFailureBroadcast() {
        long nowTime = System.currentTimeMillis();
        synchronized (HhHttp.class) {
            if (nowTime - tokenFailureBroadcastTime < TOKEN_FAILURE_INTERVAL) {
                return;
            }
            tokenFailureBroadcastTime = nowTime;
        }
        HhApplication.getInstance().sendBroadcast(new android.content.Intent().setAction(SPValue.TOKEN_FAILURE));
    }

    private static Callback getCallback(final Callback callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isTokenFailure(e)) {
                    sendTokenFailureBroadcast();
                    return;
                }
                if (callback != null) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isTokenFailure(response)) {
                    sendTokenFailureBroadcast();
                    if (response.body() != null) {
                        response.body().close();
                    }
                    return;
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        };
    }

    private static org.xutils.common.Callback.CommonCallback<String> getCallback(final org.xutils.common.Callback.CommonCallback<String> callback) {
        return new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (isTokenFailure(ex)) {
                    sendTokenFailureBroadcast();
                    return;
                }
                if (callback != null) {
                    callback.onError(ex, isOnCallback);
                }
            }

            @Override
            public void onCancelled(org.xutils.common.Callback.CancelledException cex) {
                if (callback != null) {
                    callback.onCancelled(cex);
                }
            }

            @Override
            public void onFinished() {
                if (callback != null) {
                    callback.onFinished();
                }
            }
        };
    }

    public static class TokenFailureRequestTracker implements RequestTracker {
        @Override
        public void onWaiting(RequestParams params) {
        }

        @Override
        public void onStart(RequestParams params) {
        }

        @Override
        public void onRequestCreated(UriRequest request) {
        }

        @Override
        public void onCache(UriRequest request, Object result) {
        }

        @Override
        public void onSuccess(UriRequest request, Object result) {
        }

        @Override
        public void onCancelled(UriRequest request) {
        }

        @Override
        public void onError(UriRequest request, Throwable ex, boolean isCallbackError) {
            if (isTokenFailure(ex)) {
                sendTokenFailureBroadcast();
            }
        }

        @Override
        public void onFinished(UriRequest request) {
        }
    }
}
