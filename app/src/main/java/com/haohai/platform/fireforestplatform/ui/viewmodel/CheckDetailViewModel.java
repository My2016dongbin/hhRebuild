package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.CheckDetailActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.DisposeResult;
import com.haohai.platform.fireforestplatform.ui.bean.PostDispose;
import com.haohai.platform.fireforestplatform.ui.multitype.Check;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

public class CheckDetailViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public String id;
    public Check check = new Check();
    public DisposeResult disposeResult = new DisposeResult();
    public final MutableLiveData<Check> checkDetail = new MutableLiveData<>();
    public int maxPicture = 3;
    public List<DisposeResult.Picture> pictureList = new ArrayList<>();
    public List<String> imgStrList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((CheckDetailActivity)context).finish();
    }

    public void getInfo(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_CHECK_DETAIL)
                .addParams("id",id)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("GET_IDENTIFY_DETAIL" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                check = new Gson().fromJson(String.valueOf(obj),Check.class);
                                checkDetail.postValue(check);

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

    public String parseStatus(Check message) {
        String result;
        if(Objects.equals(message.getIsHandle(), "1")){
            if(Objects.equals(message.getIsReal(), "1")){
                result = "真实";
            }else{
                result = "疑似";
            }
        }else{
            result = "未处理";
        }
        return result;
    }

    public void postDispose() {
        if((!pictureList.isEmpty()) && pictureList.get(0).getUri()!=null){
            loading.setValue(new LoadingEvent(true,"正在保存图片.."));
            PostFormBuilder postFormBuilder = HhHttp.post()
                    .url(URLConstant.POST_PICTURE);
            try{
                for (int i = 0; i < pictureList.size(); i++) {
                    Uri uri = pictureList.get(i).getUri();
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
                                    
                                    postCommit();
                                } else {
                                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
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
        }else{
            postCommit();
        }

    }

    private void postCommit() {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        PostDispose dispose = new PostDispose();
        dispose.setAlgorithmAlarmId(id);
        dispose.setContent(disposeResult.getRemark());
        for (int m = 0; m < imgStrList.size(); m++) {
            String url = imgStrList.get(m);
            if(m == 0){
                dispose.setPic1Path(url);
            }
            if(m == 1){
                dispose.setPic2Path(url);
            }
            if(m == 2){
                dispose.setPic3Path(url);
            }
        }
        String content = new Gson().toJson(dispose);
        HhHttp.postString()
                .url(URLConstant.POST_CHECK_CONFIRM)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("POST_CHECK_CONFIRM" + content);
                        HhLog.e("POST_CHECK_CONFIRM" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                            getInfo();

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
