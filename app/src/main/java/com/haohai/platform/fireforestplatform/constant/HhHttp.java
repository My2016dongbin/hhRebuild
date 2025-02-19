package com.haohai.platform.fireforestplatform.constant;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.helper.Constants;
import com.haohai.platform.fireforestplatform.utils.HhLog;
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
import org.xutils.x;

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
    public static PostFormBuilder post() {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        postFormBuilder.addHeader("requestSource", "app");
        postFormBuilder.addHeader("source", "APP");}

        return postFormBuilder;
    }

    public static GetBuilder get() {
        GetBuilder getBuilder = OkHttpUtils.get();
        getBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        getBuilder.addHeader("requestSource", "app");
        getBuilder.addHeader("source", "APP");}
        //getBuilder.addHeader("NetworkType", "Internet");

        return getBuilder;
    }
    public static OtherRequestBuilder put() {
        OtherRequestBuilder builder = OkHttpUtils.put();
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
            if(Constants.ADDING_TYPE){
//        builder.addHeader("requestSource", "app");
        builder.addHeader("source", "APP");}
        //builder.mediaType(MediaType.parse("application/json; charset=utf-8"));

        return builder;
    }

    public static PostStringBuilder postString() {
        PostStringBuilder postStringBuilder = OkHttpUtils.postString();
        postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        if(Constants.ADDING_TYPE){
//        postStringBuilder.addHeader("requestSource", "app");
        postStringBuilder.addHeader("source", "APP");}

        return postStringBuilder;
    }
    public static PostStringBuilder postStringNotToken() {
        PostStringBuilder postStringBuilder = OkHttpUtils.postString();
        //postStringBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        postStringBuilder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        if(Constants.ADDING_TYPE){
//        postStringBuilder.addHeader("requestSource", "app");
        postStringBuilder.addHeader("source", "APP");}

        return postStringBuilder;
    }

    public static PostFileBuilder postFile() {
        PostFileBuilder postFileBuilder = OkHttpUtils.postFile();
        postFileBuilder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        postFileBuilder.addHeader("requestSource", "app");
        postFileBuilder.addHeader("source", "APP");}
        postFileBuilder.mediaType(MediaType.parse("multipart/form-data"));

        return postFileBuilder;
    }

    public static void method(String method,String url, String content, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.method(method, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content));
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        builder.addHeader("requestSource", "app");
        builder.addHeader("source", "APP");}
        okHttpClient.newCall(builder.build()).enqueue(callback);
    }

    public static void methodNoApp(String method,String url, String content, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.method(method, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content));
        builder.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        builder.addHeader("requestSource", "app");
        //builder.addHeader("source", "APP");
        }
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
        if(Constants.ADDING_TYPE){
//        params.addHeader("requestSource", "app");
        params.addHeader("source", "APP");}
        params.setConnectTimeout(20000);
        x.http().post(params, callback);
    }
    public static void getX(RequestParams params,org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        params.addHeader("requestSource", "app");
        params.addHeader("source", "APP");}
        params.setConnectTimeout(20000);
        x.http().get(params, callback);
    }
    public static void methodX(HttpMethod method, RequestParams params, org.xutils.common.Callback.CommonCallback<String> callback){
        params.addHeader("Authorization", "Bearer " + SPUtils.get(HhApplication.getInstance(), SPValue.token, ""));
        if(Constants.ADDING_TYPE){
//        params.addHeader("requestSource", "app");
        params.addHeader("source", "APP");}
        params.setConnectTimeout(20000);
        x.http().request(method,params, callback);
    }
}
