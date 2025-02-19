package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.ui.activity.SignActivity;
//import com.haohai.platform.fireforestplatform.ui.activity.VideoActivity;

public class VideoViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
//        ((VideoActivity)context).finish();
    }
}
