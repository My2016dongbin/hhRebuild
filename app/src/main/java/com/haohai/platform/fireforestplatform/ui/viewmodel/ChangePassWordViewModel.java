package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.ChangePassWordActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.HhToast;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePassWordViewModel extends BaseViewModel {
    public Context context;
    public List<News> newsList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ChangePassWordActivity)context).finish();
    }

    public void submit(String oldStr,String newStr){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.method("PUT", URLConstant.PUT_CHANGE_PASSWORD, new Gson().toJson(new CommonParams("", oldStr, newStr)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loading.postValue(new LoadingEvent(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                HhLog.e("submit " + string);
                loading.postValue(new LoadingEvent(false));
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    Looper.prepare();
                    if(Objects.equals(code, "200")){
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        ((ChangePassWordActivity)context).finish();
                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
