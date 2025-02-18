package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class MainViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
}
