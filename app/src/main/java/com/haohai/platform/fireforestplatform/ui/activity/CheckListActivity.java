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
import com.haohai.platform.fireforestplatform.databinding.ActivityCheckListBinding;
import com.haohai.platform.fireforestplatform.ui.fragment.CheckFragment;
import com.haohai.platform.fireforestplatform.ui.viewmodel.CheckListViewModel;
import com.haohai.platform.fireforestplatform.utils.FragmentAdapter;

import java.util.ArrayList;

public class CheckListActivity extends BaseLiveActivity<ActivityCheckListBinding, CheckListViewModel> {


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
        binding.topBar.title.setText("督导核实");
        initPager();
    }

    private void bind_() {

    }

    @Override
    protected ActivityCheckListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_check_list);
    }

    private void initPager(){
        obtainViewModel().fragments = new ArrayList<>();
        obtainViewModel().fragments.add(CheckFragment.newInstance("全部"));
        obtainViewModel().fragments.add(CheckFragment.newInstance("待核实"));
        obtainViewModel().fragments.add(CheckFragment.newInstance("已核实"));

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
    public CheckListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(CheckListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}