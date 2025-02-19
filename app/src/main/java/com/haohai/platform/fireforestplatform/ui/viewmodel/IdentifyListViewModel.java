package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.ui.activity.IdentifyListActivity;

import java.util.ArrayList;
import java.util.List;

public class IdentifyListViewModel extends BaseViewModel {
    public Context context;
    public List<Fragment> fragments = new ArrayList<>();
    public final String[] mTitles = {"全部","未处理","真实","疑似"};
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((IdentifyListActivity)context).finish();
    }
}
