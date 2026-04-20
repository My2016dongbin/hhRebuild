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
import com.haohai.platform.fireforestplatform.databinding.ActivityXhUploadListBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.XhUploadRecord;
import com.haohai.platform.fireforestplatform.ui.multitype.XhUploadRecordViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.XhUploadListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class XhUploadListActivity extends BaseLiveActivity<ActivityXhUploadListBinding, XhUploadListViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().refreshData();
    }

    private void init_() {
        binding.topBar.title.setText("巡护上报列表");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(true);
        binding.monitorFireSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.monitorFireSmart.setRefreshFooter(new ClassicsFooter(this));
        binding.monitorFireSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().refreshData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().loadMoreData();
            }
        });
        obtainViewModel().adapter.register(XhUploadRecord.class, new XhUploadRecordViewBinder(this));
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {
    }

    @Override
    protected ActivityXhUploadListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_xh_upload_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public XhUploadListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(XhUploadListViewModel.class);
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
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
}
