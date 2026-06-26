package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityStudyBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.TutorialItem;
import com.haohai.platform.fireforestplatform.ui.multitype.TutorialItemViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.StudyViewModel;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

/**
 * Created by qc
 * on 2026/6/26.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class StudyActivity extends BaseLiveActivity<ActivityStudyBinding, StudyViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("使用教程");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);
        obtainViewModel().adapter.register(TutorialItem.class, new TutorialItemViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
        obtainViewModel().adapter.notifyDataSetChanged();
    }

    private void bind_() {

    }

    @Override
    protected ActivityStudyBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_study);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public StudyViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(StudyViewModel.class);
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
    }
}
