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
import com.haohai.platform.fireforestplatform.ui.activity.HandleDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.IdentifyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.DisposeResult;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.ui.bean.PostDispose;
import com.haohai.platform.fireforestplatform.ui.cell.PostCheck;
import com.haohai.platform.fireforestplatform.ui.multitype.Handle;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

import static com.haohai.platform.fireforestplatform.utils.ImageUtils.rotaingImageView;

public class HandleDetailViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public String id;
    public Handle handle = new Handle();
    public DisposeResult disposeResult = new DisposeResult();
    public CheckResult checkResult = new CheckResult();
    public final MutableLiveData<Handle> handleDetail = new MutableLiveData<>();
    public int maxPicture = 3;
    public List<DisposeResult.Picture> pictureList = new ArrayList<>();
    public List<String> imgStrList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((HandleDetailActivity)context).finish();
    }

    public void getInfo(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_IDENTIFY_DETAIL)
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
                                handle = new Gson().fromJson(String.valueOf(obj),Handle.class);
                                handleDetail.postValue(handle);

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

    public String parseStatus(Handle message) {
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
                .url(URLConstant.POST_HANDLE_UPLOAD)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("POST_HANDLE_UPLOAD" + content);
                        HhLog.e("POST_HANDLE_UPLOAD" + response);
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

    public void postCheck() {
        loading.setValue(new LoadingEvent(true,"提交中.."));
        String content = new Gson().toJson(new PostCheck(id, checkResult.getRemark()));
        HhHttp.postString()
                .url(URLConstant.POST_IDENTIFY_CHECK)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("POST_IDENTIFY_CHECK" + content);
                        HhLog.e("POST_IDENTIFY_CHECK" + response);
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
