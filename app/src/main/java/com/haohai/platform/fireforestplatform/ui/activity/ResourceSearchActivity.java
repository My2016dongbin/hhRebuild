package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityResourceSearchBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ResourceSearchViewModel;

public class ResourceSearchActivity extends BaseLiveActivity<ActivityResourceSearchBinding, ResourceSearchViewModel>{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init_() {
        binding.topBar.title.setText("" +
                "资源检索");
    }

    private void bind_() {

    }

    @Override
    protected ActivityResourceSearchBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_resource_search);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ResourceSearchViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ResourceSearchViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}