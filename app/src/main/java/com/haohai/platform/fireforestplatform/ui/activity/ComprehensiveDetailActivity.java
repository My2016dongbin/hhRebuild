package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityComprehensiveDetailBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ComprehensiveDetailViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;

public class ComprehensiveDetailActivity extends BaseLiveActivity<ActivityComprehensiveDetailBinding, ComprehensiveDetailViewModel> {


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
        binding.topBar.title.setText("综合检查详情");
        initUI();
    }

    private void initUI() {
        binding.textTitle.setText(CommonData.comprehensiveList.getName());
        binding.textGrid.setText(CommonData.comprehensiveList.getGridName());
        binding.textCheckTime.setText(CommonData.comprehensiveList.getStartTime());
        binding.textZz.setText(CommonData.comprehensiveList.getCreateUser());
        binding.textInfo.setText(CommonData.comprehensiveList.getDescription());
    }

    private void bind_() {

    }

    @Override
    protected ActivityComprehensiveDetailBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_comprehensive_detail);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ComprehensiveDetailViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ComprehensiveDetailViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}