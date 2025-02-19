package com.haohai.platform.fireforestplatform.ui.activity.shibei;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTroubleshootingBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityWranManagementBinding;
import com.haohai.platform.fireforestplatform.ui.activity.AddingDangerActivity;
import com.haohai.platform.fireforestplatform.ui.fragment.AlarmFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.DangerFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.EmergencyFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.ExamineFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.SafetyFragment;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TroubleshootingViewModel;

import java.util.ArrayList;

public class TroubleshootingActivity extends BaseLiveActivity<ActivityTroubleshootingBinding, TroubleshootingViewModel> {


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
        binding.topBar.title.setText("隐患排查");
        binding.topBar.right.setText("添加");
        initPager();
    }

    private void bind_() {
        binding.topBar.right.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddingDangerActivity.class);
            intent.putExtra("danger",binding.tab.getSelectedTabPosition()==0);
            startActivity(intent);
        });
    }

    @Override
    protected ActivityTroubleshootingBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_troubleshooting);
    }

    private void initPager(){
        obtainViewModel().fragments = new ArrayList<>();
        obtainViewModel().fragments.add(new DangerFragment());
        obtainViewModel().fragments.add(new SafetyFragment());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),getLifecycle(),obtainViewModel().fragments);
        binding.pager.setAdapter(adapter);
//        binding.pager.setScrollContainer(true);
        binding.pager.setUserInputEnabled(false);
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
    public TroubleshootingViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TroubleshootingViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}