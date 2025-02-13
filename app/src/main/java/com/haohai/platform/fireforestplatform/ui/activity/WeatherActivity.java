package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivitySignBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityWeatherBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SignViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.WeatherViewModel;

public class WeatherActivity extends BaseLiveActivity<ActivityWeatherBinding, WeatherViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("天气");
    }

    private void bind_() {

    }

    @Override
    protected ActivityWeatherBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_weather);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public WeatherViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(WeatherViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}