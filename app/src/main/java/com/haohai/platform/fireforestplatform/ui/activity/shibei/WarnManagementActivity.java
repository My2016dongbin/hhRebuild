package com.haohai.platform.fireforestplatform.ui.activity.shibei;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityWranManagementBinding;
import com.haohai.platform.fireforestplatform.event.OpenFilter;
import com.haohai.platform.fireforestplatform.ui.activity.TaskListInfoActivity;
import com.haohai.platform.fireforestplatform.ui.fragment.AlarmFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.EmergencyFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.ExamineFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MainFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MessageFragment;
import com.haohai.platform.fireforestplatform.ui.fragment.MineFragment;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskListViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TaskViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.WarnManagementViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class WarnManagementActivity extends BaseLiveActivity<ActivityWranManagementBinding, WarnManagementViewModel> {


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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        binding.topBar.title.setText("报警信息");
        binding.topBar.rightIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_fliter));
        binding.topBar.rightIcon.setVisibility(View.VISIBLE);
        initPager();
    }

    private void bind_() {

    }

    @Override
    protected ActivityWranManagementBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_wran_management);
    }

    private void initPager(){
        obtainViewModel().fragments = new ArrayList<>();
        obtainViewModel().fragments.add(new EmergencyFragment());
        obtainViewModel().fragments.add(new AlarmFragment());
        /*obtainViewModel().fragments.add(new ExamineFragment());*/

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),getLifecycle(),obtainViewModel().fragments);
        binding.pager.setAdapter(adapter);
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
        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    binding.topBar.rightIcon.setVisibility(View.VISIBLE);
                }else{
                    binding.topBar.rightIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public WarnManagementViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(WarnManagementViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}