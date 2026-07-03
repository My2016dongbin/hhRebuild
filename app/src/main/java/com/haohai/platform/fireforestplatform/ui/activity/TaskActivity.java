package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskBinding;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskListViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TaskViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.drakeet.multitype.MultiTypeAdapter;

public class TaskActivity extends BaseLiveActivity<ActivityTaskBinding, TaskViewModel> implements TaskListViewBinder.OnItemClickListener {

    private static final String[] TAB_TITLES = {"全部", "未开始", "执行中", "已结束"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        obtainViewModel().id = intent.getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init_() {
        binding.topBar.title.setText("任务单");
        initTabs();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(true);
        binding.monitorFireSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.monitorFireSmart.setRefreshFooter(new ClassicsFooter(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.monitorFireSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                binding.monitorFireSmart.setNoMoreData(false);
                obtainViewModel().refreshData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().loadMoreData();
            }
        });

        TaskListViewBinder taskListViewBinder = new TaskListViewBinder(this);
        taskListViewBinder.setListener(this);
        obtainViewModel().adapter.register(TaskList.class, taskListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void initTabs() {
        binding.tabLayout.removeAllTabs();
        for (String title : TAB_TITLES) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title));
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) {
                    return;
                }
                switchTaskStatus(mapPositionToStatus(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        updateTabSelection(obtainViewModel().status.getValue());
    }

    private void switchTaskStatus(String status) {
        String currentStatus = obtainViewModel().status.getValue();
        if ((status == null && currentStatus == null) || (status != null && status.equals(currentStatus))) {
            return;
        }
        obtainViewModel().switchStatus(status);
    }

    private void updateTabSelection(String status) {
        if (binding.tabLayout.getTabCount() == 0) {
            return;
        }
        int position = mapStatusToPosition(status == null ? TaskViewModel.STATUS_ALL : status);
        TabLayout.Tab tab = binding.tabLayout.getTabAt(position);
        if (tab != null && !tab.isSelected()) {
            tab.select();
        }
    }

    private String mapPositionToStatus(int position) {
        if (position == 1) {
            return TaskViewModel.STATUS_NOT_STARTED;
        }
        if (position == 2) {
            return TaskViewModel.STATUS_IN_PROGRESS;
        }
        if (position == 3) {
            return TaskViewModel.STATUS_FINISHED;
        }
        return TaskViewModel.STATUS_ALL;
    }

    private int mapStatusToPosition(String status) {
        if (TaskViewModel.STATUS_NOT_STARTED.equals(status)) {
            return 1;
        }
        if (TaskViewModel.STATUS_IN_PROGRESS.equals(status)) {
            return 2;
        }
        if (TaskViewModel.STATUS_FINISHED.equals(status)) {
            return 3;
        }
        return 0;
    }

    ///推送任务刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageRefresh event) {
        obtainViewModel().refreshData();
    }

    private void bind_() {

    }

    @Override
    protected ActivityTaskBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_task);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TaskViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TaskViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().status.observe(this, this::updateTabSelection);
        obtainViewModel().refreshEvent.observe(this, value -> binding.monitorFireSmart.finishRefresh());
        obtainViewModel().loadMoreEvent.observe(this, value -> binding.monitorFireSmart.finishLoadMore());
        obtainViewModel().noMoreData.observe(this, noMore -> {
            if (Boolean.TRUE.equals(noMore)) {
                binding.monitorFireSmart.finishLoadMoreWithNoMoreData();
            } else {
                binding.monitorFireSmart.setNoMoreData(false);
            }
        });
    }

    @Override
    public void onItemClick(TaskList taskList) {
        Intent intent = new Intent(this, TaskListInfoActivity.class);
        intent.putExtra("id",taskList.getId());
        startActivity(intent);
    }
}
