package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityLevelFireBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityMonitorFireBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.LevelFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.LevelFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LevelFireMessageListViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MonitorFireMessageListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class LevelFireMessageListActivity extends BaseLiveActivity<ActivityLevelFireBinding, LevelFireMessageListViewModel> implements LevelFireMessageViewBinder.OnItemClickListener {

    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("火警等级");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.monitorFireSmart.setRefreshHeader(new ClassicsHeader(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.monitorFireSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().postData(id);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        LevelFireMessageViewBinder levelFireMessageViewBinder = new LevelFireMessageViewBinder(this);
        levelFireMessageViewBinder.setListener(this);
        obtainViewModel().adapter.register(LevelFireMessage.class, levelFireMessageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {

    }

    @Override
    protected ActivityLevelFireBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_level_fire);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LevelFireMessageListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LevelFireMessageListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
    }

    @Override
    public void onItemClick(LevelFireMessage levelFireMessage) {
        /*Intent intent = new Intent(this, MonitorFireMessageInfoActivity.class);
        intent.putExtra("id",levelFireMessage.getId());
        startActivity(intent);*/
    }
}