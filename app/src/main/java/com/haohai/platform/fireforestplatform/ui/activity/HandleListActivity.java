package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityHandleListBinding;
import com.haohai.platform.fireforestplatform.ui.fragment.HandleFragment;
import com.haohai.platform.fireforestplatform.ui.viewmodel.HandleListViewModel;
import com.haohai.platform.fireforestplatform.utils.FragmentAdapter;

import java.util.ArrayList;

public class HandleListActivity extends BaseLiveActivity<ActivityHandleListBinding, HandleListViewModel> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init_() {
        binding.topBar.title.setText("处置管理");
        initPager();
    }

    private void bind_() {

    }

    @Override
    protected ActivityHandleListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_handle_list);
    }

    private void initPager(){
        obtainViewModel().fragments = new ArrayList<>();
        obtainViewModel().fragments.add(HandleFragment.newInstance("全部"));
        obtainViewModel().fragments.add(HandleFragment.newInstance("已处置"));
        obtainViewModel().fragments.add(HandleFragment.newInstance("未处置"));

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),getLifecycle(),obtainViewModel().fragments);
        binding.pager.setAdapter(adapter);
        //binding.pager.setUserInputEnabled(false);
        //TabLayout 和 Viewpager2 关联
        TabLayoutMediator tab = new TabLayoutMediator(binding.tab, binding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(obtainViewModel().mTitles[position]);
                //tab.setIcon(obtainViewModel().mTitles[position]);
            }
        });
        tab.attach();
        binding.tab.setTabIndicatorFullWidth(false);

    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public HandleListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(HandleListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}