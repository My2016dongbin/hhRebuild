package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityKkFireBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityMonitorFireBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.KKFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.KKFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.KKFireMessageListViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MonitorFireMessageListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class KKFireMessageListActivity extends BaseLiveActivity<ActivityKkFireBinding, KKFireMessageListViewModel> implements KKFireMessageViewBinder.OnItemClickListener {

    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        init_();
        bind_();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("卡口报警");

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

        KKFireMessageViewBinder kkFireMessageViewBinder = new KKFireMessageViewBinder(this);
        kkFireMessageViewBinder.setListener(this);
        obtainViewModel().adapter.register(KKFireMessage.class, kkFireMessageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {

    }

    @Override
    protected ActivityKkFireBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_kk_fire);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public KKFireMessageListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(KKFireMessageListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
    }

    @Override
    public void onItemClick(KKFireMessage kkFireMessage) {
        Intent intent = new Intent(this, MonitorFireMessageInfoActivity.class);
        intent.putExtra("id",kkFireMessage.getId());
        startActivity(intent);
    }
}