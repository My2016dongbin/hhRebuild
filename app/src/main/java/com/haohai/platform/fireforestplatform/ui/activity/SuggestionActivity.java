package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivitySuggestionBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Suggestion;
import com.haohai.platform.fireforestplatform.ui.multitype.SuggestionViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SuggestionViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class SuggestionActivity extends BaseLiveActivity<ActivitySuggestionBinding, SuggestionViewModel> implements SuggestionViewBinder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    @Override
    protected void onResume() {
        super.onResume();

        obtainViewModel().page = 1;
        obtainViewModel().postData();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        binding.topBar.title.setText("投诉建议");
        binding.topBar.rightIcon.setVisibility(View.VISIBLE);
        binding.topBar.rightIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        binding.topBar.setOnRightClick(view -> {
            Intent intent = new Intent(this, SuggestionInfoActivity.class);
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.newsSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.newsSmart.setEnableLoadMore(true);
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.newsSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page=1;
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

        SuggestionViewBinder newsViewBinder = new SuggestionViewBinder(this);
        newsViewBinder.setListener(this);
        obtainViewModel().adapter.register(Suggestion.class, newsViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {
        
    }

    @Override
    protected ActivitySuggestionBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_suggestion);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SuggestionViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SuggestionViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().loadMore.observe(this, integer -> {
            if(integer == 0){
                binding.newsSmart.setEnableLoadMore(false);
            }else if(integer == 1){
                binding.newsSmart.setEnableLoadMore(true);
            }
        });
    }

    @Override
    public void onItemClick(Suggestion news) {
        Intent intent = new Intent(this, SuggestionInfoActivity.class);
        intent.putExtra("id",news.getId());
        CommonData.suggestion = news;
        startActivity(intent);
    }
}