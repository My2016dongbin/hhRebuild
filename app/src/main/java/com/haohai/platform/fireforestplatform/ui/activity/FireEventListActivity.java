package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityFireEventListBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.FireEvent;
import com.haohai.platform.fireforestplatform.ui.multitype.FireEventViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FireEventListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class FireEventListActivity extends BaseLiveActivity<ActivityFireEventListBinding, FireEventListViewModel> implements FireEventViewBinder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postData();
    }

    private void init_() {
        binding.topBar.title.setText("事件列表");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.fireEventSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.fireEventSmart.setEnableLoadMore(true);

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.fireEventSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page = 1;
                obtainViewModel().postData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postData();
                refreshLayout.finishLoadMore(1000);
            }
        });

        FireEventViewBinder fireEventViewBinder = new FireEventViewBinder(this);
        fireEventViewBinder.setListener(this);
        obtainViewModel().adapter.register(FireEvent.class, fireEventViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {

    }

    @Override
    protected ActivityFireEventListBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_fire_event_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public FireEventListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FireEventListViewModel.class);
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().loadMore.observe(this, integer -> {
            if(integer == 0){
                binding.fireEventSmart.setEnableLoadMore(false);
            }else if(integer == 1){
                binding.fireEventSmart.setEnableLoadMore(true);
            }
        });
    }

    @Override
    public void onItemClick(FireEvent fireEvent) {
        obtainViewModel().onItemClick(fireEvent);
    }
}
