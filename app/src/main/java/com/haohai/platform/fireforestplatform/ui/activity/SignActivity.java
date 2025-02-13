package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivitySignBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SignViewModel;

public class SignActivity extends BaseLiveActivity<ActivitySignBinding, SignViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("考勤管理");
    }

    private void bind_() {

    }

    @Override
    protected ActivitySignBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_sign);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SignViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SignViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}