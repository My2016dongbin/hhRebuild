package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.ui.activity.HandleListActivity;

import java.util.ArrayList;
import java.util.List;

public class HandleListViewModel extends BaseViewModel {
    public Context context;
    public List<Fragment> fragments = new ArrayList<>();
    public final String[] mTitles = {"全部","已处置","未处置"};
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((HandleListActivity)context).finish();
    }
}
