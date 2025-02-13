package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoStreamActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class VideoStreamViewModel extends BaseViewModel {
    public Context context;
    public List<News> newsList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((VideoStreamActivity)context).finish();
    }

}
