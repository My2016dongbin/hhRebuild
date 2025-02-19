package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.ui.activity.TbsActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.News;

import java.util.ArrayList;
import java.util.List;

public class TbsViewModel extends BaseViewModel {
    public Context context;
    public List<News> dataList = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((TbsActivity)context).finish();
    }

}
